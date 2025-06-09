- Coding Nuggets
  - Median --not_exist--> deequ --use--> ApproxQuantile
  - Runners
    - ColumnProfilerRunner
    - ConstraintSuggestionRunner    
  - Profiler --uses--> Mean
  - constraint Val --uses--> hasAverage
  - for_each --not--> foreach

- Nuances
  - deequ --not_provide--> col counts
    - deequ --purpose--> Validation/Profiling --not--> df Structure Analysis
    - df.shape inspection --right_after--> ingestion --in--> Initial Inspction Stage (std approach)

- Goal
  - Profiling Tool Goal ----> Understand Data (Auto) --suggest--> Data Quality Constraints (Auto)

- Data Profiling Introduction
  - Already Covered

- ColumnProfileRunner
  - ColumnProfileRunner ----> deequ API component ----> Data Profiling
    - data type
    - Completeness
    - approx uniques
    - Numericals : min, max, mean etc
    - Numericals : Histograms (AWS has it)
    - Categorical : Histograms
    - Completeness
    - Patterns (Regex Summaries etc)

- Loops in Deequ
  - for ----> rarely used
  - foreach ----> often used

- Code
```
import com.amazon.deequ.profiles.ColumnProfilerRunner
import org.apache.spark.sql.SparkSession

val spark = SparkSession.builder()
  .appName("Deequ Profiling Example")
  .master("local[*]")
  .getOrCreate()

val data = spark.read.option("header", "true").option("inferSchema", "true").csv("your_data.csv")

val result = ColumnProfilerRunner()
  .onData(data)
  .run()

// View profiling results
println(result.profiles)  

// Automatically generate suggested constraints
val suggestedConstraints = result.constraintSuggestions

suggestedConstraints.foreach { case (colName, constraints) =>      // dict.items()
  println(s"Suggested constraints for $colName:")                  // print(col) like f string
  constraints.foreach(println)                                     // print(constraints, values from .items() ), remeber last 2 lines have no interrelation 
}

```

- **Understading Profiling (check below example)**
  - Get Suggested Constraints
```
import com.amazon.deequ.suggestions.{ConstraintSuggestionRunner, Rules}
import com.amazon.deequ.verification.{VerificationSuite, CheckStatus}
import com.amazon.deequ.VerificationResult
import org.apache.spark.sql.SparkSession

// Initialize SparkSession (if not already created)
val spark = SparkSession.builder()
  .appName("Deequ Constraint Suggestion and Verification")
  .getOrCreate()

// Load or have your DataFrame 'data' ready
val data = spark.read.option("header", "true").csv("s3://your-bucket/your-data.csv")

// Step 1: Suggest constraints automatically based on data profile
val suggestionResult = ConstraintSuggestionRunner()
  .onData(data)
  .addConstraintRules(Rules.DEFAULT)  // Use default built-in rules for suggestions
  .run()

// Step 2: Print suggested constraints for each column
println("Suggested constraints:")
suggestionResult.constraintSuggestions.foreach { case (column, constraints) =>
  println(s"Column: $column")
  constraints.foreach { constraint =>
    println(s" - ${constraint.description}")
  }
}

// Step 3: Verify the suggested constraints on the data
val verificationResult: VerificationResult = VerificationSuite()
  .onData(data)
  .addConstraints(suggestionResult.constraintSuggestions.flatMap(_._2))  // Flatten all suggested constraints
  .run()

// Step 4: Print verification results
println("\nVerification results:")
verificationResult.checkResults.foreach { case (check, checkResult) =>
  println(s"Check: ${check.description}")
  println(s"Status: ${checkResult.status}")

  checkResult.constraintResults.foreach { cr =>
    println(s"  Constraint: ${cr.constraint}")
    println(s"  Status: ${cr.status}")
  }
}

```
  - Sample Output
    - Suggested Constraints
    ```
    Suggested constraints:
      Column: price
       - Completeness constraint on column price
       - Minimum constraint on column price with minimum value 1.0
       - Maximum constraint on column price with maximum value 9999.99
      
      Column: userId
       - Completeness constraint on column userId
       - Uniqueness constraint on column userId

    ```   

    - Suggested Constraints with Validation Results
    ```
    Verification results:
    Check: Constraint Check
    
    Status: Success
    
      Constraint: Completeness constraint on column price
      Status: Success
    
      Constraint: Minimum constraint on column price with minimum value 1.0
      Status: Success
    
      Constraint: Maximum constraint on column price with maximum value 9999.99
      Status: Success
    
      Constraint: Completeness constraint on column userId
      Status: Success
    
      Constraint: Uniqueness constraint on column userId
      Status: Failure
    
    ```


- Common Analyzers (for Custom Profiling) ---> **no has is here**

```
import com.amazon.deequ.analyzers._           // Import analyzers
import com.amazon.deequ.AnalyzerContext        // For working with results
import com.amazon.deequ.analysis.AnalysisRunner  // To run the analysis on data

// Define analyzers for various data quality and statistical metrics

// Row Count
val rowCount = Size()  
// Total number of purchase records: basic dataset size info

// Completeness
val completenessPrice = Completeness("price")  
// % of rows where 'price' is not null: ensures price data is present

val compliancePrice = Compliance("price", "price < 10000000")  
// Fraction of rows where 'price' is under 10 million: sanity check for outliers or data errors

// Uniqueness
val uniquenessUserId = Uniqueness(Seq("userId"))  
// How unique each userId is: check for duplicates or data integrity issues

val uniquenessUserProduct = Uniqueness(Seq("userId", "productId"))  
// Uniqueness of (userId, productId) pairs: check if the same user has multiple purchase entries for the same product

// Count Metrics
val distinctnessProduct = Distinctness("productId")  
// Ratio of unique products seen in dataset: product variety measure

val approxCountDistinctProduct = ApproxCountDistinct("productId")        # approx helps in lesser computational load, gives approx values
// Approximate count of unique products for performance-friendly distinct count

val countDistinctProduct = CountDistinct("productId")  
// Exact count of unique products (more expensive computation)

// Statistical Metrics (Numeric)
val maxPrice = Maximum("price")  
// Highest price paid: identify max spend

val minPrice = Minimum("price")  
// Lowest price paid: identify any free or invalid records

val meanPrice = Mean("price")  
// Average price: general pricing trend

val stddevPrice = StandardDeviation("price")  
// Price variation: pricing consistency or anomalies

val sumSales = Sum("sales")  
// Total sales amount: business volume measure

val approxQuantilePriceMedian = ApproxQuantile("price", 0.5)  
// Median price: robust central tendency against outliers

// Correlations
val corrPriceQuantity = Correlation("price", "quantity")  
// Relationship between price and quantity bought: insight into buying behavior

// Entropy
val entropyProduct = Entropy("productId")  
// Measure of randomness/diversity in product purchases: how spread out product sales are

// Histogram
val histogramPrice = Histogram("price", maxBuckets = 10)  
// Distribution of prices bucketed into 10 buckets: visualize price ranges and density ----> on report this show like a an list of counts of 10 bins

// Now run all analyzers together and generate report
val analyzers = Seq(
  rowCount,
  completenessPrice,
  compliancePrice,
  uniquenessUserId,
  uniquenessUserProduct,
  distinctnessProduct,
  approxCountDistinctProduct,
  countDistinctProduct,
  maxPrice,
  minPrice,
  meanPrice,
  stddevPrice,
  sumSales,
  approxQuantilePriceMedian,
  corrPriceQuantity,
  entropyProduct,
  histogramPrice
)

val analysisResult = AnalysisRunner
  .onData(data)               // Your Spark DataFrame to analyze
  .addAnalyzers(analyzers)    // Add all analyzers to run
  .run()                     // Execute the analysis

// Convert results into a DataFrame for easier viewing / saving / processing
val metrics = AnalyzerContext.successMetricsAsDataFrame(spark, analysisResult)

// Show all metrics nicely, no truncation
metrics.show(truncate = false)

// Save to Amazon S3
metrics.write.mode("overwrite").parquet("s3://your-bucket-name/metrics_parquet/")

```
- Final Profiling Report Example (Dataset or Tables are Same, just synonyms, default is df)

| entity  | instance | name              | value                                                                                                                                              |
| ------- | -------- | ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| Dataset | \*       | Size              | 10000                                                                                                                                              |
| Dataset | price    | Completeness      | 0.98                                                                                                                                               |
| Dataset | price    | Maximum           | 9999.99                                                                                                                                            |
| Dataset | price    | Minimum           | 1.50                                                                                                                                               |
| Dataset | price    | Mean              | 500.75                                                                                                                                             |
| Dataset | price    | StandardDeviation | 120.45                                                                                                                                             |
| Dataset | price    | Histogram         | {"bins":\["\[1.5, 200.0)", "\[200.0, 400.0)", "\[400.0, 600.0)", "\[600.0, 800.0)", "\[800.0, 9999.99]"], "values":\[2000, 3500, 2500, 1500, 500]} |


- **Tips and Best Practices**
  - Profiling --important--> First Foundation ----> Review & Customize --avoid--> Rework
  - Validate ----> Dev Data --before--> Production
  - Combine ----> Profiling Results + Domain Knowledge (sku formats, price limits etc)
  - Profiling Results --perform--> Monitoring
  - Profiling --integrate--> MetricsRepository

  
