package test;

import ca.ubc.dnslookup.RecordType;
import ca.ubc.dnslookup.ResourceRecord;

import java.util.HashSet;
import java.util.Set;

public class HexDumps {

    public static final String STANFORD_EDU_RAW_RESPONSE =
            "                              00 04 81 80 00 01\n" +
                    "00 05 00 00 00 00 03 77 77 77 08 73 74 61 6e 66\n" +
                    "6f 72 64 03 65 64 75 00 00 01 00 01 c0 0c 00 05\n" +
                    "00 01 00 00 07 07 00 1b 0c 73 74 61 6e 66 6f 72\n" +
                    "64 68 73 31 37 08 77 70 65 6e 67 69 6e 65 03 63\n" +
                    "6f 6d 00 c0 2e 00 05 00 01 00 00 00 77 00 11 0e\n" +
                    "6c 62 6d 61 73 74 65 72 2d 39 36 35 35 38 c0 3b\n" +
                    "c0 55 00 05 00 01 00 00 00 77 00 46 2b 63 6c 75\n" +
                    "73 74 65 72 39 36 2d 65 6c 62 77 70 65 65 6c 2d\n" +
                    "31 6b 61 79 6b 68 6e 39 76 32 64 32 38 2d 32 30\n" +
                    "37 35 37 33 39 34 36 30 09 75 73 2d 65 61 73 74\n" +
                    "2d 31 03 65 6c 62 09 61 6d 61 7a 6f 6e 61 77 73\n" +
                    "c0 44 c0 72 00 01 00 01 00 00 00 3b 00 04 22 c1\n" +
                    "8e e0 c0 72 00 01 00 01 00 00 00 3b 00 04 22 ce\n" +
                    "ac 0f";

    public static final Set<ResourceRecord> STANFORD_EDU_EXPECTED_RECORD_SET = new HashSet<>();
    /*static {
        STANFORD_EDU_EXPECTED_RECORD_SET.add(new ResourceRecord(
                "www.stanford.edu"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ,
                RecordType.getByCode(5) ,
                1799,
                "stanfordhs17.wpengine.com"
        ));
        STANFORD_EDU_EXPECTED_RECORD_SET.add(new ResourceRecord(
                "stanfordhs17.wpengine.com"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ,
                RecordType.getByCode(5) ,
                119,
                "lbmaster-96558.wpengine.com"
        ));
        STANFORD_EDU_EXPECTED_RECORD_SET.add(new ResourceRecord(
                "lbmaster-96558.wpengine.com"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ,
                RecordType.getByCode(5) ,
                70,
                "cluster96-elbwpeel-1kaykhn9v2d28-2075739460.us-east-1.elb.amazonaws.com"
        ));
        try {
            STANFORD_EDU_EXPECTED_RECORD_SET.add(new ResourceRecord(
                    "cluster96-elbwpeel-1kaykhn9v2d28-2075739460.us-east-1.elb.amazonaws.com"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ,
                    RecordType.getByCode(1) ,
                    59,
                    InetAddress.getByAddress(new byte[] {34,(byte) 193,(byte) 142,(byte) 224})
            ));
            STANFORD_EDU_EXPECTED_RECORD_SET.add(new ResourceRecord(
                    "cluster96-elbwpeel-1kaykhn9v2d28-2075739460.us-east-1.elb.amazonaws.com"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ,
                    RecordType.getByCode(1) ,
                    59,
                    InetAddress.getByAddress(new byte[] {34,(byte) 206,(byte) 172,(byte) 15})

            ));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }*/

    public static final String STANFORD_NS_RAW_RESPONSE =
            "b2 1e 80 00 00 01 00 00 00 06 00 07 03 77 77 77\n" +
            "08 73 74 61 6e 66 6f 72 64 03 65 64 75 00 00 01\n" +
            "00 01 c0 19 00 02 00 01 00 02 a3 00 00 13 01 61\n" +
            "0b 65 64 75 2d 73 65 72 76 65 72 73 03 6e 65 74\n" +
            "00 c0 19 00 02 00 01 00 02 a3 00 00 04 01 63 c0\n" +
            "30 c0 19 00 02 00 01 00 02 a3 00 00 04 01 64 c0\n" +
            "30 c0 19 00 02 00 01 00 02 a3 00 00 04 01 66 c0\n" +
            "30 c0 19 00 02 00 01 00 02 a3 00 00 04 01 67 c0\n" +
            "30 c0 19 00 02 00 01 00 02 a3 00 00 04 01 6c c0\n" +
            "30 c0 2e 00 01 00 01 00 02 a3 00 00 04 c0 05 06\n" +
            "1e c0 4d 00 01 00 01 00 02 a3 00 00 04 c0 1a 5c\n" +
            "1e c0 5d 00 01 00 01 00 02 a3 00 00 04 c0 1f 50\n" +
            "1e c0 6d 00 01 00 01 00 02 a3 00 00 04 c0 23 33\n" +
            "1e c0 7d 00 01 00 01 00 02 a3 00 00 04 c0 2a 5d\n" +
            "1e c0 8d 00 01 00 01 00 02 a3 00 00 04 c0 29 a2\n" +
            "1e c0 7d 00 1c 00 01 00 02 a3 00 00 10 20 01 05\n" +
            "03 cc 2c 00 00 00 00 00 00 00 02 00 36\n";

    public static final Set<ResourceRecord> STANFORD_NS_RESPONSE_EXPECTED = new HashSet<>();
    static {
        String[] nsTypes = new String[] {"a", "c", "d", "f", "g", "l"};
        for (String type: nsTypes) {
            STANFORD_NS_RESPONSE_EXPECTED.add(new ResourceRecord(
                    "edu",
                    RecordType.NS,
                    172800,
                    type + ".edu-servers.net"
            ));
        }
    }

    public static final String NO_SUCH_NAME_RAW_RESPONSE =
            "5925 8403 0001 0000 0001 0000 0962 6c75\n" +
            "6562 6572 7279 0575 6772 6164 0263 7303\n" +
            "7562 6302 6361 0000 0100 01c0 1600 0600\n" +
            "0100 000e 1000 2203 6e73 31c0 1c05 6272\n" +
            "656e 74c0 1c56 1545 ea00 0054 6000 0003\n" +
            "8400 093a 8000 00a8 c0";

    public static final Set<ResourceRecord> NO_SUCH_NAME_EXPECTED_RESPONSE = new HashSet<>();

    public static final String GOOGLE_MX_RAW_RESPONSE =
            "00 0a 81 80 00 01 00 05 00 00 00 00 06 67 6f 6f\n" +
            "67 6c 65 03 63 6f 6d 00 00 0f 00 01 c0 0c 00 0f\n" +
            "00 01 00 00 00 e6 00 11 00 1e 04 61 6c 74 32 05\n" +
            "61 73 70 6d 78 01 6c c0 0c c0 0c 00 0f 00 01 00\n" +
            "00 00 e6 00 09 00 14 04 61 6c 74 31 c0 2f c0 0c\n" +
            "00 0f 00 01 00 00 00 e6 00 04 00 0a c0 2f c0 0c\n" +
            "00 0f 00 01 00 00 00 e6 00 09 00 28 04 61 6c 74\n" +
            "33 c0 2f c0 0c 00 0f 00 01 00 00 00 e6 00 09 00\n" +
            "32 04 61 6c 74 34 c0 2f\n";

    public static final Set<ResourceRecord> GOOGLE_MX_EXPECTED_RESPONSE = new HashSet<>();
    static {
        String[] prefaces = new String[] {"", "alt1.", "alt2.", "alt3.", "alt4."};
    }

    public static final String GOOGLE_SOA_RESPONSE_RAW =
            "00 0c 81 80 00 01 00 01 00 00 00 00 06 67 6f 6f\n" +
            "67 6c 65 03 63 6f 6d 00 00 06 00 01 c0 0c 00 06\n" +
            "00 01 00 00 00 04 00 26 03 6e 73 31 c0 0c 09 64\n" +
            "6e 73 2d 61 64 6d 69 6e c0 0c 15 8c 63 ce 00 00\n" +
            "03 84 00 00 03 84 00 00 07 08 00 00 00 3c\n";

    public static Set<ResourceRecord> GOOGLE_SOA_EXPECTED_RESPONSE = new HashSet<>();

    public static String EXPECTED_SIMPLE_QUERY_CS_UBC_CA =
            "001b0000000100000000000003777777" +
            "026373037562630263610000010001";

}
