package test;

import ca.ubc.cs317.dnslookup.DNSCache;
import ca.ubc.cs317.dnslookup.DNSQueryHandler;
import ca.ubc.cs317.dnslookup.ResourceRecord;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Set;

import static org.junit.Assert.*;
import static test.HexDumps.*;

public class DNSQueryHandlerTests {

    DNSCache emptyCache;

    @Before
    public void init() {
        emptyCache = new DNSCache();
    }

    @Test
    public void parseResponseWithMultipleAnswers() {
        int TRANSACTION_ID = 0x4;
        try {
            Set<ResourceRecord> resourceRecordSet = DNSQueryHandler.decodeAndCacheResponse(
                    TRANSACTION_ID,
                    byteBufferFromHexString(STANFORD_EDU_RAW_RESPONSE.replaceAll("\\s+", "")),
                    emptyCache);
            assertEquals(STANFORD_EDU_EXPECTED_RECORD_SET, resourceRecordSet);
        } catch (UnknownHostException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseResponseWithNameServers() {
        int TRANSACTION_ID = 0xb21e;

        try {
            Set<ResourceRecord> resourceRecordSet = DNSQueryHandler.decodeAndCacheResponse(
                    TRANSACTION_ID,
                    byteBufferFromHexString(STANFORD_NS_RAW_RESPONSE.replaceAll("\\s+", "")),
                    emptyCache);
            assertEquals(STANFORD_NS_RESPONSE_EXPECTED, resourceRecordSet);
        } catch (UnknownHostException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseMxResponse() {
        int TRANSACTION_ID = 0x12;

        try {
            Set<ResourceRecord> resourceRecordSet = DNSQueryHandler.decodeAndCacheResponse(
                    TRANSACTION_ID,
                    byteBufferFromHexString(GOOGLE_MX_RAW_RESPONSE.replaceAll("\\s+", "")),
                    emptyCache
            );
            assertEquals(GOOGLE_MX_EXPECTED_RESPONSE, resourceRecordSet);
        } catch (UnknownHostException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseSoaResponse() {
        try {
            Set<ResourceRecord> resourceRecordSet = DNSQueryHandler.decodeAndCacheResponse(
                    1,
                    byteBufferFromHexString(GOOGLE_SOA_RESPONSE_RAW.replaceAll("\\s+", "")),
                    emptyCache
            );
            assertEquals(GOOGLE_SOA_EXPECTED_RESPONSE, resourceRecordSet);
        } catch (UnknownHostException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseNoSuchNameResponse() {
        try {
            Set<ResourceRecord> resourceRecordSet = DNSQueryHandler.decodeAndCacheResponse(
                    1,
                    byteBufferFromHexString(NO_SUCH_NAME_RAW_RESPONSE.replaceAll("\\s+", "")),
                    emptyCache
            );
            assertEquals(NO_SUCH_NAME_EXPECTED_RESPONSE, resourceRecordSet);
        } catch (UnknownHostException e) {
            fail(e.getMessage());
        }
    }

    private ByteBuffer byteBufferFromHexString(String packet) {
        int length = packet.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) (((Character.digit(packet.charAt(i), 16) << 4)) +
                    Character.digit(packet.charAt(i + 1), 16));
        }
        return ByteBuffer.wrap(data);
    }



}
