- IMportant
  - Deequ --only--> Unit Tests --no_suppport--> Column Value Updation | Adding New Columns etc manipulations 

- All Built-in Constraints   
  - .isComplete("column_name") : no nans
  - .isUnique("column_name") : no dups
  - .isNonNegative("column_name") : values >= 0
  - .isContainedIn("column_name", Array("A", "B", "C") ) : within a set (no Outliers - no support for infs)
  - .hasPattern("colmns_name", "SKU-\\d{5}") : regex matching
  - .hasMin( "column_name", _ > 10 )   // min value in col satisifies expression, _ is lambda in Python
  - .hasMax("column_name", _ < 100)   // max value in col satisifies expression, _ is lambda in Python
  - .hasDataType : Col data type enforcement
  - .isApproximatelyUnique("column_name", fraction)` : Approx Uniqueness (eg 95%) ----> 95% of values in col must be unique
  - .satisfies() : Custom bool expression

- Custom Constraints
  - .satisfies("col >= 1 AND col <= 100", "within-range_check")
  - `check.where("region == 'EU'")`
              `.isBetween("price", 9.0, 180.0)`
  - `check.where("region == 'APAC'")`
              `.isContainedIn("currency", ARRAY("CNY", "JPY", "INR"))`
  - `check.hasSatisfying("usd_price / local_price", _ >= 0.5)`
  - `check.hasCompletness("benchmarkprices")`
         `.where("Competitor IS NOT NULL")`
  - `check.isLessThanOrEqual("launch_date", todayDate)`
  - `check.where("country == 'IND'")`
          `.isComplete("mrp")`
  - `check.where("country == 'US'")`
         `.satisfies("taxes_included",_ == false)`

- Separate Dozens Checks
```
val check = Check(CheckLevel.Error, "Data quality checks")
  .isComplete("benchmarkprices").where("Competitor IS NOT NULL")
  .isNonNegative("price").where("country = 'US'")
  .satisfies("taxes_included = false", "US Taxes Check", Some("country = 'US'"))
  // and so on...
```

- Run Mutiple Checks
```
val verificationResult = VerificationSuite()
  .onData(yourDataFrame)
  .addCheck(check1)
  .addCheck(check2)
  .run()
```

- Check Levels (just like Python Code Errors)
  - CheckLevel.Error ----> Critical failure --needs--> immediate attention
  - CheckLevel.Warning ----> Cautionary Warning
  - CheckLevel.Info — Informational --just-->provide details --not_necessarily--> Problem

- Some vs Where
  - Read Below Examples ----> Where --affects--> All Constraints (def after, in Same Check)
    - Apply ----> Where Clause ----> Before (only) 
  - Read Below Examples ----> Some --affects--> Given Constraint (Only)
    - Apply ----> Some Clause ----> After (only) 


```
val check = Check(CheckLevel.Error, "Checks")
  .isComplete("purchase_amount", Some("region = 'US'"))
  .isNonNegative("purchase_amount", Some("region = 'US'"))
  .isComplete("price", Some("region = 'EU'"))
```

```
val checkUS = Check(CheckLevel.Error, "US Checks")
  .where("region = 'US'")
  .isComplete("purchase_amount")
  .isNonNegative("purchase_amount")

val checkEU = Check(CheckLevel.Error, "EU Checks")
  .where("region = 'EU'")
  .isComplete("price")

```

- Statistical Constraints in Practice for CPG
  - Statistical Metrics (Numeric)
    - hasMax(col, _ <= 1000)
    - hasMin(col, _ <= 1000)
    - hasAverage(col, _ <= 1000)
    - hasStandardDeviation(col, _ <= 1000)
    - hasSum(col, _ == 100)
    - hasApproxQuantile("price", 0.95, _<=1500)
  - Correlations
    - hasCorrelation(col1, col2, _>=0.9)
  - Entropy
    - .hasEntropy("columnName", _ <= expectedValue)
  - Histogram
    - even distribution -----> .hasHistogram("price", 10, hist => hist.values.forall(count => count >= 0.05 * hist.values.sum))     //
each of 10 bins has 5% rows
    -  unexpectedly high sales ----> .hasHistogram("quantity_sold", 8, hist => hist.values.exists(count => count > 0.4 * hist.values.sum) == false)  // no bin has more than 40% rows
    - All Bins have values ----> .hasHistogram("product_age_days", 12, hist => hist.values.forall(_ > 0))  // Product Ages cover all 12 bins
    - skewness ---->
      - .hasHistogram("price", 20, hist => 
      - hist.values.take(3).sum <= 0.05 * hist.values.sum &&       // Left tail ≤ 5%
      - hist.values.takeRight(3).sum <= 0.05 * hist.values.sum )    // Right tail ≤ 5%

