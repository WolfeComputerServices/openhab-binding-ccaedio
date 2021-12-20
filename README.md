# CCAEdio Binding

This binding is meant for learning coachs of students enrolled in the Commonwealth Chart Acadamy. It provides the ability to automate based on when students are in school, if a student has any overdue events and upcoming events.

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

## Full Example

_Provide a full usage example based on textual configuration files (*.things, *.items, *.sitemap)._