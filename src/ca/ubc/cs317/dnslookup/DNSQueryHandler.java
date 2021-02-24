package ca.ubc.cs317.dnslookup;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Set;

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
        addHeader(message);
        byte[] query = newQueryWithQuestion(message, node);

        byte[] buffer = new byte[65508];
        DatagramPacket queryPacket = new DatagramPacket(query, query.length, server, DEFAULT_DNS_PORT);
        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

        socket = new DatagramSocket();
        socket.send(queryPacket);
        socket.receive(responsePacket);

        byte[] responseData = responsePacket.getData();
        byte[] responseId = new byte[2];
        responseId[0] = responseData[0];
        responseId[1] = responseData[1];

        return new DNSServerResponse(ByteBuffer.wrap(responseData), ByteBuffer.wrap(responseId).getShort());
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
        // number of authority PRs
        // number of additional PRs
        for (int i = 6; i < 12; i++) {
            message[i] = 0x00;
        }
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
                                                             DNSCache cache) {
        // TODO (PART 1): Implement this
        return null;
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

