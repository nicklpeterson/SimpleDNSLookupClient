package ca.ubc.cs317.dnslookup;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static test.HexDumps.EXPECTED_SIMPLE_QUERY_CS_UBC_CA;

public class DNSQueryHandler {

    private static final int DEFAULT_DNS_PORT = 53;
    private static DatagramSocket socket;
    private static boolean verboseTracing = false;

    /**
     * Sets up the socket and set the timeout to 5 seconds
     *
     * @throws SocketException if the socket could not be opened, or if there was an
     *                         error with the underlying protocol
     */
    public static void openSocket() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(5000);
    }

    /**
     * Closes the socket
     */
    public static void closeSocket() {
        socket.close();
    }

    /**
     * Set verboseTracing to tracing
     */
    public static void setVerboseTracing(boolean tracing) {
        verboseTracing = tracing;
    }

    /**
     * Builds the query, sends it to the server, and returns the response.
     *
     * @param message Byte array used to store the query to DNS servers.
     * @param server  The IP address of the server to which the query is being sent.
     * @param node    Host and record type to be used for search.
     * @return A DNSServerResponse Object containing the response buffer and the transaction ID.
     * @throws IOException if an IO Exception occurs
     */
    public static DNSServerResponse buildAndSendQuery(byte[] message, InetAddress server,
                                                      DNSNode node) throws IOException {
        //TODO: add verbose logging
        // TODO: add timeout if no response received. Retry the request 10 times.
        addHeader(message);
        byte[] query = newQueryWithQuestion(message, node);

        byte[] buffer = new byte[65508];
        DatagramPacket queryPacket = new DatagramPacket(query, query.length, server, DEFAULT_DNS_PORT);
        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

        openSocket();
        socket.send(queryPacket);
        socket.receive(responsePacket);

        byte[] responseData = responsePacket.getData();
        byte[] responseId = new byte[2];
        responseId[0] = responseData[0];
        responseId[1] = responseData[1];

        return new DNSServerResponse(ByteBuffer.wrap(responseData), ByteBuffer.wrap(responseId).getShort());
    }

    private static ByteBuffer byteBufferFromHexString(String packet) {
        int length = packet.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) (((Character.digit(packet.charAt(i), 16) << 4)) +
                    Character.digit(packet.charAt(i + 1), 16));
        }
        return ByteBuffer.wrap(data);
    }

    private static void addHeader(byte[] message) {
        final Random random = new Random();
        final byte[] id = new byte[2];
        random.nextBytes(id);
        // Id
        message[0] = id[0];
        message[1] = id[1];
        // flags
        message[2] = 0x01;
        message[3] = 0x00;
        // number of questions
        message[4] = 0x00;
        message[5] = 0x01;
        // number of answer PRs
        message[6] = 0x00;
        message[7] = 0x00;
        // number of authority PRs
        message[8] = 0x00;
        message[9] = 0x00;
        // number of additional PRs
        message[10] = 0x00;
        message[11] = 0x00;
    }

    private static byte[] newQueryWithQuestion(byte[] message, DNSNode dnsNode) {
        final byte[] name = dnsNode.getHostName().getBytes();
        byte initialByte = 0;
        for (int i = 0; name[i] != 0x2e; i++) {
            initialByte++;
        }
        int msgIndex = 12;
        message[msgIndex++] = initialByte;

        for (int i = 0; i < name.length; i++) {
            byte b = name[i];
            if (b == 0x2e) {
                int k = i + 1;
                byte sectionSize = 0;
                while (k < name.length && name[k] != 0x2e) {
                    sectionSize++;
                    k++;
                }
                b = sectionSize;
            }
            message[msgIndex++] = b;
        }

        message[msgIndex++] = 0x0;
        message[msgIndex++] = 0x0;
        final byte[] type = ByteBuffer.allocate(4).putInt(dnsNode.getType().getCode()).array();
        message[msgIndex++] = type[3];
        message[msgIndex++] = 0x0;
        message[msgIndex] = 0x01;
        return trimUnusedBytes(message, msgIndex);
    }

    private static byte[] trimUnusedBytes(byte[] message, int index){
        final byte[] query = new byte[index + 1];

        int i = 0;
        for (byte b : message) {
            if (i <= index) {
                query[i] = b;
                i++;
            } else {
                break;
            }
        }
        return query;
    }

    /**
     * Decodes the DNS server response and caches it.
     *
     * @param transactionID  Transaction ID of the current communication with the DNS server
     * @param responseBuffer DNS server's response
     * @param cache          To store the decoded server's response
     * @return A set of resource records corresponding to the name servers of the response.
     */
    public static Set<ResourceRecord> decodeAndCacheResponse(int transactionID, ByteBuffer responseBuffer,
                                                             DNSCache cache) throws UnknownHostException {
        // TODO (PART 1): Implement this
        byte[] response = responseBuffer.array();
        int numberOfRecords = getNumberOfRecords(response);
        int answerIndex = getAnswerIndex(response);

        // Get Query DNSNode
        // DNSNode dnsNode = getQueryDnsNode(response, nameIndex);
        Set<ResourceRecord> recordSet = new HashSet<>();
        for (int i = 0; i < numberOfRecords; i++) {

            String hostName = getHostName(response, answerIndex);
            RecordType recordType = getResponseType(response, answerIndex);
            long ttl = getTTL(response, answerIndex);
            int dataLength = getDataLength(response, answerIndex);

            // If record type is other skip the record. TODO: Make sure that is correct
            ResourceRecord resourceRecord;
            if (recordType == RecordType.CNAME || recordType == RecordType.NS || recordType == RecordType.SOA) {
                String cname = parseName(response, answerIndex + 12);
                resourceRecord = new ResourceRecord(hostName, recordType, ttl, cname);
                cache.addResult(resourceRecord);
                recordSet.add(resourceRecord);
            } else if (recordType == RecordType.MX) {
                String mxName = parseName(response, answerIndex + 14);
                resourceRecord = new ResourceRecord(hostName, recordType, ttl, mxName);
                cache.addResult(resourceRecord);
                recordSet.add(resourceRecord);
            } else if (recordType == RecordType.AAAA){
                InetAddress ip = getIpv6Address(response, answerIndex);
                resourceRecord = new ResourceRecord(hostName, recordType, ttl, ip);
                cache.addResult(resourceRecord);
                recordSet.add(resourceRecord);
            } else if (recordType == RecordType.A) {
                InetAddress ip = getIpv4Address(response, answerIndex);
                resourceRecord = new ResourceRecord(hostName, recordType, ttl, ip);
                cache.addResult(resourceRecord);
                recordSet.add(resourceRecord);
            }

            // advance index to next answer
            answerIndex += dataLength + 12;
        }

        return recordSet;
    }

    private static int getNumberOfRecords(byte[] response) {
        int records = 0;
        for (int i = 6; i < 12; i+=2) {
            byte[] bytes = new byte[] {response[i], response[i+1]};
            records += ByteBuffer.wrap(bytes).getShort();
        }
        return records;
    }

    private static String getHostName(byte[] response, int answerIndex) {
        byte bitmask = 0b00111111;
        byte[] nameLocation = new byte[] {(byte) (bitmask & response[answerIndex]), response[answerIndex + 1]};
        int hostIndex = ByteBuffer.wrap(nameLocation).getShort();
        return parseName(response, hostIndex);
    }

    private static RecordType getResponseType(byte[] response, int answerIndex) {
        byte[] type = new byte[] {response[answerIndex + 2], response[answerIndex + 3]};
        return RecordType.getByCode(ByteBuffer.wrap(type).getShort());
    }

    private static int getDataLength(byte[] response, int answerIndex) {
        byte[] length = new byte[] {response[answerIndex + 10], response[answerIndex + 11]};
        return ByteBuffer.wrap(length).getShort();
    }

    private static InetAddress getIpv4Address(byte[] response, int answerIndex) throws UnknownHostException {
        byte[] ipBytes = new byte[4];
        System.arraycopy(response, answerIndex + 12, ipBytes, 0, 4);
        return InetAddress.getByAddress(ipBytes);
    }

    private static InetAddress getIpv6Address(byte[] response, int answerIndex) throws UnknownHostException {
        byte[] ipBytes = new byte[16];
        System.arraycopy(response, answerIndex + 12, ipBytes, 0, 16);
        return InetAddress.getByAddress(ipBytes);
    }

    private static int getQDCount(byte[] response) {
        byte[] qdCount = new byte[2];
        qdCount[0] = response[4];
        qdCount[1] = response[5];
        return ByteBuffer.wrap(qdCount).getShort();
    }

    private static int getAnswerIndex(byte[] response) {
        int qIndex = 12;
        for (int i = getQDCount(response); i > 0; i--) {
            while (!isEndOfQuery(response, qIndex)) {
                qIndex++;
            }
            qIndex += 4;
        }
        return qIndex;
    }

    private static String parseName(byte[] response, int nameIndex) {
        if (response[nameIndex] == (byte) 0xc0) {
            return getHostName(response, nameIndex);
        }
        byte[] nameBytes = new byte[512];
        int sectionSize = response[nameIndex];
        int nameByteIndex = 0;
        int responseIndex = nameIndex + 1;
        while(sectionSize > 0) {
            for (int i = 0; i < sectionSize; i++) {
                nameBytes[nameByteIndex++] = response[responseIndex++];
            }
            nameBytes[nameByteIndex++] = 0x2e;
            sectionSize = 0x000000FF & response[responseIndex++];
            if (sectionSize == 0xc0) {
                byte[] trimmedBytes = Arrays.copyOfRange(nameBytes, 0, nameByteIndex);
                return (new String(trimmedBytes, StandardCharsets.UTF_8)) + getHostName(response, responseIndex - 1);
            }
        }
        nameBytes[nameByteIndex - 1] = 0;
        return (new String(nameBytes, StandardCharsets.UTF_8)).trim();
    }

    private static long getTTL(byte[] response, int answerIndex) {
        byte[] ttlBytes = new byte[4];
        for (int i = answerIndex + 6, k = 0; i < answerIndex + 10; i++, k++) {
            ttlBytes[k] = response[i];
        }
        return ByteBuffer.wrap(ttlBytes).getInt();
    }

    private static boolean isEndOfQuery(byte[] response, int index) {
        return isValidType(response, index) && isValidClass(response, index + 2);
    }

    private static boolean isValidType(byte[] response, int index) {
        byte first = response[index];
        byte second = response[index + 1];
        return first == 0 && (second == 1 || second == 5 || second == 6 || second == 15 || second == 28);
    }

    private static boolean isValidClass(byte[] response, int index) {
        return response[index] == 0 && response[index + 1] == 1;
    }

    /**
     * Formats and prints record details (for when trace is on)
     *
     * @param record The record to be printed
     * @param rtype  The type of the record to be printed
     */
    private static void verbosePrintResourceRecord(ResourceRecord record, int rtype) {
        if (verboseTracing)
            System.out.format("       %-30s %-10d %-4s %s\n", record.getHostName(),
                    record.getTTL(),
                    record.getType() == RecordType.OTHER ? rtype : record.getType(),
                    record.getTextResult());
    }
}

