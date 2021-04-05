Simple DNS Server

## Run

There is a compiled jar in this repository. Run with the following command:

        java -jar DNSLookupService.jar 198.162.35.1
Replace 198.162.35.1 with the ip address of any known DNS server. This will be the default DNS Server, it can be
 changed later when the program is running.
 
## Compile
 
If you want to make a fresh build. I use the following commands to compile the program:

        dir /s /B .\src\ca\*.java > sources.txt
        javac @sources.txt -d bin
        jar cvfe DNSLookupService.jar ca.ubc.dnslookup.DNSLookupService -C bin ca/
        
The build.bat file will execute these commands for you or you can build program however you like.
## Usage

Lookup the ip address for a given url address. Type is one of A, AAAA, NS, MX, or CNAME

        lookup [url] [type]

Turn trace on/off. When trace is on the program will log all iterative DNS requests. Note, all lookups are done
 iteratively.

        trace on|off
        
Change the target DNS server

        server [ip address]
        
Clear the address cache

        dump
        
Quit

        quit