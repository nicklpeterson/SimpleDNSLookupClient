rmdir /Q /S bin
del sources.txt
mkdir bin
dir /s /B .\src\ca\*.java > sources.txt
javac @sources.txt -d bin
jar cvfe DNSLookupService.jar ca.ubc.dnslookup.DNSLookupService -C bin ca/
