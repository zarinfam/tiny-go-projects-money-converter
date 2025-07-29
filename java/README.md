# Money Converter

A Java CLI application that converts currency by calling the European Central Bank HTTP service. This is a Java 24 reimplementation of the original Go application, compiled to a native binary using GraalVM.

## Features

- Convert between different currencies using live exchange rates
- Supports all major world currencies
- Fixed-precision decimal arithmetic to avoid floating-point errors
- Native binary compilation with GraalVM for fast startup
- HTTP timeout handling
- XML parsing of ECB exchange rate data

## Requirements

- Java 24
- GraalVM (for native compilation)
- Maven 3.6+

## Building

### Standard JAR
```bash
mvn clean package
```

### Native Binary (GraalVM)
```bash
mvn clean package -Pnative
```

This will create a native executable named `money-converter` in the `target` directory.

## Usage

```bash
# Using JAR
java -jar target/money-converter-1.0.0.jar -from USD -to EUR 100

# Using native binary
./target/money-converter -from USD -to EUR 100
```

### Arguments

- `-from <currency>`: Source currency (required)
- `-to <currency>`: Target currency (default: EUR)
- `<amount>`: Amount to convert (required)

### Examples

```bash
# Convert 100 USD to EUR
./money-converter -from USD -to EUR 100

# Convert 50 GBP to USD  
./money-converter -from GBP -to USD 50

# Convert 1000 JPY to EUR (default target)
./money-converter -from JPY 1000
```

## Supported Currencies

The application supports all currencies provided by the European Central Bank exchange rate service, including:

- Major currencies: USD, EUR, GBP, JPY, CHF, CAD, AUD, etc.
- Special precision handling for currencies like:
  - IRR (0 decimal places)
  - MGA, MRU, CNY, VND (1 decimal place)
  - BHD, IQD, KWD, LYD, OMR, TND (3 decimal places)
  - All others (2 decimal places)

## Architecture

The application is structured into several packages:

- `com.moneyconverter.money`: Core money handling classes (Currency, Amount, Decimal)
- `com.moneyconverter.ecbank`: ECB HTTP client and XML parsing
- `com.moneyconverter`: Main CLI application

## Error Handling

The application handles various error conditions:

- Invalid currency codes
- Network timeouts
- Malformed amounts
- ECB service errors
- Precision overflow

## Native Image

The GraalVM native image configuration includes:

- Reflection configuration for XML parsing
- HTTP client support
- Resource configuration
- No fallback mode for smaller binaries

## License

MIT License