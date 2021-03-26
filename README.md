# executor-json-library
 Simple json library for KayJam Executor

## How to use
### Decode
```
var jsonEncoded = "{\"test\":123}"
var decoded = JSON::decode(jsonEncoded);

println(decoded);
```
Output: 
```
[test:123]
```
