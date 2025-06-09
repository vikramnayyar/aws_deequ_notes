- Classes in Scala ----> Thousands of Class-Like Constructs ----> 
  - Optional FYI
  | Type                      | Description                                                                              |
  | ------------------------- | ---------------------------------------------------------------------------------------- |
  | `class`                   | Basic class definition, similar to Java classes.                                         |
  | `case class`              | Special class with built-in immutability, pattern matching, and concise syntax.          |
  | `abstract class`          | Like in Java, cannot be instantiated; meant to be extended.                              |
  | `trait`                   | Similar to Java interfaces, but can also contain method implementations.                 |
  | `object`                  | Singleton instance, used for static members or utilities.                                |
  | `companion object`        | An `object` with the same name as a class, used for factory methods or shared utilities. |
  | `enum` (Scala 3)          | Enumeration types, added in Scala 3 for more expressive enums.                           |
  | `type class` (conceptual) | A pattern for ad-hoc polymorphism using implicits or type classes (like in Haskell).     |

  - case class Examples
  | Case Class         | Purpose                                                                  |
  | ------------------ | ------------------------------------------------------------------------ |
  | `ConstraintResult` | Represents the result of a single constraint evaluation.                 |
  | `AnalyzerContext`  | Holds metrics calculated by analyzers.                                   |
  | `AnalysisResult`   | Wraps the results of running an `AnalysisRunner`.                        |
  | `ResultKey`        | Identifies a result in a metrics repository (e.g., by timestamp or tag). |
  | `DoubleMetric`     | A concrete implementation of a metric.                                   |


