# Compiling protobuf files

Navigate to `Networking/src` and execute:
```
protoc --java_out=. networking/Proto/Platform.proto
```

Syntax:
```
protoc --java_out=<output folder> <proto files>
```
