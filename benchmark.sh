#!/bin/bash

# Number of executions (default: 5)
NUM_EXECUTIONS=${1:-5}
# Number of warm-up executions (default: 2)
NUM_WARMUP=${2:-2}

echo "=== Benchmarking Currency Converter Implementations ==="
echo "Number of warm-up executions: $NUM_WARMUP"
echo "Number of measured executions: $NUM_EXECUTIONS"
echo

# Check if hyperfine is installed
if [ ! -x "/usr/bin/hyperfine" ]; then
    echo "Error: hyperfine is not installed."
    echo "Please install hyperfine to run this benchmark."
    exit 1
fi

echo "Using hyperfine for benchmarking"


# Function to extract statistics from hyperfine JSON output
extract_stat() {
    local json_file=$1
    local stat_name=$2
    
    # Use grep and cut to extract the statistic value from the JSON file
    # This is a simple approach that works for the specific JSON structure of hyperfine
    grep "\"$stat_name\":" "$json_file" | cut -d':' -f2 | tr -d ' ,' | awk '{printf "%.0f", $1 * 1000}'
}

echo "=== Java Implementation ==="
# Clean any previous builds
cd java
rm -f target/money-converter

# Measure compile time using hyperfine (single run, no warmup)
echo "Compiling Java version..."
JAVA_COMPILE_JSON_FILE=$(mktemp)
hyperfine --runs 1 \
          --command-name "Java-Compile" \
          --export-json "$JAVA_COMPILE_JSON_FILE" \
          "mvn clean package -Pnative -q -T 1C -Dmaven.test.skip=true > /dev/null 2>&1"

# Extract compile time from hyperfine output
JAVA_COMPILE_TIME=$(extract_stat "$JAVA_COMPILE_JSON_FILE" "mean")
echo "Compile time: $JAVA_COMPILE_TIME ms"

# Get binary size
JAVA_SIZE=$(ls -lh target/money-converter | awk '{print $5}')
echo "Binary size: $JAVA_SIZE"

# Measure execution time using hyperfine
echo "Measuring execution time using hyperfine..."
JAVA_JSON_FILE=$(mktemp)
hyperfine --warmup "$NUM_WARMUP" --runs "$NUM_EXECUTIONS" \
          --command-name "Java" \
          --export-json "$JAVA_JSON_FILE" \
          "./target/money-converter -from USD -to EUR 100"

# Extract statistics from hyperfine output
JAVA_MIN_EXEC=$(extract_stat "$JAVA_JSON_FILE" "min")
JAVA_MAX_EXEC=$(extract_stat "$JAVA_JSON_FILE" "max")
JAVA_MEDIAN_EXEC=$(extract_stat "$JAVA_JSON_FILE" "median")
JAVA_AVG_EXEC=$(extract_stat "$JAVA_JSON_FILE" "mean")

echo "Execution time statistics:"
echo "  Min: $JAVA_MIN_EXEC ms"
echo "  Max: $JAVA_MAX_EXEC ms"
echo "  Median: $JAVA_MEDIAN_EXEC ms"
echo "  Average: $JAVA_AVG_EXEC ms"
echo

# Return to root directory
cd ..

echo "=== Go Implementation ==="
# Clean any previous builds
cd go
rm -f bin/moneyconverter

# Measure compile time using hyperfine (single run, no warmup)
echo "Compiling Go version..."
GO_COMPILE_JSON_FILE=$(mktemp)
hyperfine --runs 1 \
          --command-name "Go-Compile" \
          --export-json "$GO_COMPILE_JSON_FILE" \
          "go build -o bin/moneyconverter . > /dev/null 2>&1"

# Extract compile time from hyperfine output
GO_COMPILE_TIME=$(extract_stat "$GO_COMPILE_JSON_FILE" "mean")
echo "Compile time: $GO_COMPILE_TIME ms"

# Get binary size
GO_SIZE=$(ls -lh bin/moneyconverter | awk '{print $5}')
echo "Binary size: $GO_SIZE"

# Measure execution time using hyperfine
echo "Measuring execution time using hyperfine..."
GO_JSON_FILE=$(mktemp)
hyperfine --warmup "$NUM_WARMUP" --runs "$NUM_EXECUTIONS" \
          --command-name "Go" \
          --export-json "$GO_JSON_FILE" \
          "./bin/moneyconverter -from USD -to EUR 100"

# Extract statistics from hyperfine output
GO_MIN_EXEC=$(extract_stat "$GO_JSON_FILE" "min")
GO_MAX_EXEC=$(extract_stat "$GO_JSON_FILE" "max")
GO_MEDIAN_EXEC=$(extract_stat "$GO_JSON_FILE" "median")
GO_AVG_EXEC=$(extract_stat "$GO_JSON_FILE" "mean")

echo "Execution time statistics:"
echo "  Min: $GO_MIN_EXEC ms"
echo "  Max: $GO_MAX_EXEC ms"
echo "  Median: $GO_MEDIAN_EXEC ms"
echo "  Average: $GO_AVG_EXEC ms"
echo

# Return to root directory
cd ..

echo "=== Comparison Summary ==="
echo "Metric                | Java Native | Go"
echo "--------------------- | ----------- | ----------"
echo "Compile time          | $JAVA_COMPILE_TIME ms | $GO_COMPILE_TIME ms"
echo "Binary size           | $JAVA_SIZE | $GO_SIZE"
echo "Execution time (min)  | $JAVA_MIN_EXEC ms | $GO_MIN_EXEC ms"
echo "Execution time (max)  | $JAVA_MAX_EXEC ms | $GO_MAX_EXEC ms"
echo "Execution time (med)  | $JAVA_MEDIAN_EXEC ms | $GO_MEDIAN_EXEC ms"
echo "Execution time (avg)  | $JAVA_AVG_EXEC ms | $GO_AVG_EXEC ms"

# Cleanup temporary files
rm -f "$JAVA_JSON_FILE" "$GO_JSON_FILE" "$JAVA_COMPILE_JSON_FILE" "$GO_COMPILE_JSON_FILE"