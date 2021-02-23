rmdir /Q /S bin
del sources.txt
mkdir bin
dir /s /B *.java > sources.txt
javac @sources.txt -d bin
jar cvfe DNSLookupService.jar ca.ubc.cs317.dnslookup.DNSLookupService -C bin ca/
