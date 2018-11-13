# SQLyourCSV
Your goal is to write a program which will allow users to query their csv files using SQL. CSV tables should act as tables in the RDBMS.

## Requirements
The marketing has already prepared a pricing plan for the application. It will also serve as a list of requirements.

## Basic version (free)
Allows:

  - querying single csv file.
  - selecting which columns should be displayed (SELECT * FROM xyz.csv; should also work).
  - filtering data (using WHERE, >, <, =, <>, LIKE, AND, OR)
  
## Pro version (15$/month)
Allows everything from the basic version plus:

  - aggregating data using SUM, AVG MAX, MIN

## Enterprise version (30$/month)
Allows everything from the pro version plus:

  - filtering aggregates using HAVING
  - joining data between multiple .csv files. (using JOIN abc.csv ON ...)

## DeLuxe version (100$/month)
Allows everything from the pro version plus:

  - updating data using UPDATE
  - deleting data using DELETE
  
#  Goals for the week:

  - practice functional programming
  - work with third party SDK
  - get hands on experience with DevOps
  - deploy at least one of your applications
