// Import Libraries
import com.amazon.deequ.VerificationSuite
import com.amazon.deequ.checks.{Check, CheckLevel}    // CheckLevel = {Error, Warning}
import com.amazon.deequ.VerificationResult            //  holds result after run
import org.apache.spark.sql.SparkSession              // Spark Session

// 1. Initialize Spark Session
val spark = SparkSession.builder()
  .appName("Deequ Example")  
  .master("local[*]")                              // run spark using all CPU cores
  .getOrCreate()                                   // creates a new Spark session or reuses existing one

// 2. read Dataframe & setup date and check val
val data = spark.read.option("header", "true").option("inferSchema", "true").csv("your_data.csv")    // inferSchema catches datatypes by itself, otherwise takes all cols as string
val todayDate = java.sql.Date.valueOf(java.time.LocalDate.now())    // no import required - Scala always gives java std lib

val check = Check(CheckLevel.Error, "Data Quality Checks")


  // 1. Range check
  .satisfies("col >= 1 AND col <= 100", "within-range_check")

  // 2. Conditional isBetween
  .isBetween("price", 9.0, 180.0, Some("region = 'EU'"))

  // 3. Conditional isContainedIn
  .isContainedIn("currency", Array("CNY", "JPY", "INR"), Some("region = 'APAC'"))

  // 4. Derived expression check
  .satisfies("usd_price / local_price >= 0.5", "Exchange Rate Check")

  // 5. Conditional completeness check
  .isComplete("benchmarkprices", Some("Competitor IS NOT NULL"))      // **Some** replaces .where

  // 6. Date comparison
  .isLessThanOrEqualTo("launch_date", todayDate.toString)

  // 7. Conditional completeness
  .isComplete("mrp", Some("country = 'IND'"))

  // 8. Conditional boolean check
  .satisfies("taxes_included = false", "US Taxes Check", Some("country = 'US'"))        // US Taxes Check is the label for Check, mandatory for .satisfies(), and not available in in built checks

val verificationResult = VerificationSuite()
  .onData(data)
  .addCheck(check)
  .run()
