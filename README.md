# CCA Edio Binding

This binding is meant for learning coachs of students enrolled in the Commonwealth Chart Acadamy (CCA). It provides the ability to automate based on when students are in school, if a student has any overdue events and upcoming events.

## Supported Things

There are two things support: Edio Account and Edio Student.

An Edio Account contains the information for the login credentials of the learning choach as well as a setting for the number of upcoming events to retrieve per day.

A Student account defines a student with their full name and id as defined in Edio.

## Discovery

The binding will discover all students enrolled in Commonwealth Charter School and registered in the Edio system.

## Binding Configuration

No binding specific configruation required.

## Thing Configuration

Edio Account things must have a coaches userid and password entered. A number of upcoming (calendar) days to retreive on a daily basis can also be defined.

Student things must have the student full name as registered at CCA. The ID can coptionally be entered. It is best to allow the discovery process fill in this information.

## Channels

Edio Account

    | channel       | type   | description                                                  |
    |---------------|--------|--------------------------------------------------------------|
    | hasSchool     | Switch | If on, students at CCA have school; if off, they do not.     |

Student things

    | channel       | type   | description                                                  |
    |---------------|--------|--------------------------------------------------------------|
    | overdueEvents | String | Contains json with student name and array of overdue events  |
    | upcomingEvents| String | Contains json with student name and array of upcomging events|
    
upcomingEvents channel contains json similiar to the following:

```json
{
  "student": "Bill Joe Bob",
    "upcoming":[
      {
        "date":{
          "iso":"2019-12-20",
          "year":2019,
          "month":12,
          "day":20
        },
        "course":"English Language Arts",
        "topic":"Researching the Holocaust: Creating Citations and Conclusion Due"
      },
      {
        "date":{
          "iso":"2019-12-21",
          "year":2019,
          "month":12,
          "day":21
        },
        "course":"Math",
        "topic":"END OF UNIT TEST"
      },
      {
        "date":{
          "iso":"2020-01-04",
          "year":2020,
          "month":1,
          "day":4
        },
        "course":"History",
        "topic":"Section 3 Quiz"
      }
    ]
  }

```
