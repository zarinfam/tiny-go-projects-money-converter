#!/bin/bash

# Number of executions (default: 3)
NUM_EXECUTIONS=${1:-3}

echo "=== Benchmarking Currency Converter Implementations ==="
echo "Number of executions per test: $NUM_EXECUTIONS"
echo

# Function to get time in milliseconds
get_time_ms() {
    echo $(($(date +%s%N)/1000000))
}

echo "=== Java Implementation ==="
# Clean any previous builds
cd java
rm -f target/money-converter

# Measure compile time
echo "Compiling Java version..."
COMPILE_START=$(get_time_ms)
mvn clean package -Pnative -q -T 1C -Dmaven.test.skip=true > /dev/null 2>&1
COMPILE_END=$(get_time_ms)
JAVA_COMPILE_TIME=$((COMPILE_END - COMPILE_START))
echo "Compile time: $JAVA_COMPILE_TIME ms"

# Get binary size
JAVA_SIZE=$(ls -lh target/money-converter | awk '{print $5}')
echo "Binary size: $JAVA_SIZE"

# Measure execution time
JAVA_TOTAL_TIME=0
i=1
while [ $i -le $NUM_EXECUTIONS ]; do
    # Measure execution time
    START=$(get_time_ms)
    ./target/money-converter -from USD -to EUR 100 > /dev/null 2>&1
    END=$(get_time_ms)
    EXEC_TIME=$((END - START))
    JAVA_TOTAL_TIME=$((JAVA_TOTAL_TIME + EXEC_TIME))
    i=$((i + 1))
done

JAVA_AVG_EXEC=$((JAVA_TOTAL_TIME / NUM_EXECUTIONS))
echo "Average execution time: $JAVA_AVG_EXEC ms"
echo

# Return to root directory
cd ..

echo "=== Go Implementation ==="
# Clean any previous builds
cd go
rm -f bin/moneyconverter

# Measure compile time
echo "Compiling Go version..."
COMPILE_START=$(get_time_ms)
go build -o bin/moneyconverter . > /dev/null 2>&1
COMPILE_END=$(get_time_ms)
GO_COMPILE_TIME=$((COMPILE_END - COMPILE_START))
echo "Compile time: $GO_COMPILE_TIME ms"

# Get binary size
GO_SIZE=$(ls -lh bin/moneyconverter | awk '{print $5}')
echo "Binary size: $GO_SIZE"

# Measure execution time
GO_TOTAL_TIME=0
i=1
while [ $i -le $NUM_EXECUTIONS ]; do
    # Measure execution time
    START=$(get_time_ms)
    ./bin/moneyconverter -from USD -to EUR 100 > /dev/null 2>&1
    END=$(get_time_ms)
    EXEC_TIME=$((END - START))
    GO_TOTAL_TIME=$((GO_TOTAL_TIME + EXEC_TIME))
    i=$((i + 1))
done

GO_AVG_EXEC=$((GO_TOTAL_TIME / NUM_EXECUTIONS))
echo "Average execution time: $GO_AVG_EXEC ms"
echo

# Return to root directory
cd ..

echo "=== Comparison Summary ==="
echo "Metric          | Java Native | Go"
echo "--------------- | ----------- | ----------"
echo "Compile time    | $JAVA_COMPILE_TIME ms | $GO_COMPILE_TIME ms"
echo "Binary size     | $JAVA_SIZE | $GO_SIZE"
echo "Execution time  | $JAVA_AVG_EXEC ms | $GO_AVG_EXEC ms"