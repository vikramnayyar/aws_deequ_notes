- Introduction
  - Deequ --library--> Python/Scala --built--> Apache Spark --defines--> unit tests --integrates--> Pipelines
  - Applications ---> Automated Data Quality Validations --on--> Large Datasets --in--> Distributed Environment

  - Deequ Automation (report generated after each step)
    - Deequ Frequency ----> Daily | Per Batch
    - Deequ Operation ----> Profiliing (Overall Health) ----> Validation (where not allowed) ----> Monitoring (Over Time)
      
      - Profiling --describe--> Baseline Statistics --generate--> Profiling Report
      - Profiling --only-->  describes --no--> Alerts
        - Col Types
        - Missing Value Counts
        - Min, max, median, sd
        - Unique Keys
        - Pattern Frequencies (eg Regex)
       
      - Validation (where not allowed - Alerts)
        - Check ----> Quality Constraints ----> Pass/Fail Report   
          - Missing Values  
          - Dups
          - Outliers
          - Schema Mismatches
        - Examples
          - Price Range : 2 to 100
          - Brand Nans : NO
          - Dataset Shape etc
                  
        - Monitoring --tracking--> Metrics (Over Time) ----> Regression | Unexpected Changes
        - Validation Metrics --not--> by itself ----> Monitored --have_to--> Track

  - Anomally vs Alerts
    - Profiling --over_period--> Anomally --requires--> Separate Coding
    - Anomally --totally--> Monitoring --only_eval--> Profiling (over time) --never_runs_on--> Validation Reports  
    - validation --gives--> Alerts (everytime) 

  - Data Quality Dimensions
    - Completeness ----> No Nans
    - Uniqueness ---> No Dups
    - Consistency ----> formats | units |
    - Accuracy --data_represents--> Real World Facts (requires domain knowledge)
    - Timeliness ----> Most Current Info (Critical for real time decision making)

  - Deequ's Architecture
    - Data Pipeline ----> Profiling Suite ----> Metrics Repo ----> Profiling Report ----> Anomally Detection (Degradation of Data Quality)
    - Data Pipeline ----> Verification Suite ----> Constraints (Data Qulaity Rules) ----> Validation report --used--> Alerts | Rule Enforcement
   
  - Deequ Pipeline Placement
    - Data Source ----> Initial Cleanup/ETL (as Source is Messy) ----> Deequ Validation ----> Data Warehouse or Lakehouse   
   
  - Deequ Integration
    - Integration --uses--> Check API
    - `import com.amazon.deequ.checks.Check`
    - `import com.amazon.deequ.Verification.Suite`
    - `val check = Check(spark, CheckLevel.Error, "Data Quality check")`
    - `      .isComplete("brand")        // no missing values in 'brand col'`
    - `      .isContainedIn("price", 2, 100)    // 'price' between 2 and 100`
    - ` val VerficationResult = VerificationSuite()`
    - `                        .onData(dataframe)`
    - `                        .addCheck(check)`
    - `                        .run()`
   
    - Limitations ----> Spark only (no small datasets) | Limited Python Support (Limited functionality, less efficient) | Batch Jobs (No realtime)
      - No realtime ----> designed on Batch (Large) Data (not instant Data Drives) | higher Latency | Metrics Aggregation over time | High Cost

 
