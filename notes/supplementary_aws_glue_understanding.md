
glue has 3 parts as below, we will have to leverage ETL and deequ together
 
Glue Crawler (Ingestion)
- Scans S3, infers schemas, partitions
- Creates/updates tables in Catalog
 
Glue Jobs (ETL + DQ)
- Performs Extract, Transform, Load (ETL), uses Spark.
- Implement custom data quality checks, including advanced validations such as:
	- Comparing current data with historical loads to detect unexpected changes or data drift
	- Cross-batch consistency and referential integrity checks
	- Complex anomaly and outlier detection 
	- Custom conditional validations involving multiple fields or external references.
- Integrates with AWS Deequ for standardized and automated DQ constraints checks, run on individual datasets and check column profiles, below are examples:
	- Duplicates, Uniqueness, Completeness(nans) and null rate monitoring.
	- statistical & outlier profiling (mean, median, mode, or custom)
	- Pattern compliance (like string matching on IDs, timestamps increasing)
 
- Glue Data Catalog (configuring tables)
	- Centralized metadata repository for schemas, tables, partitions (user defined and automated)
