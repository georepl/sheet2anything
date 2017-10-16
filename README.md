# sheet2anything

You've got a spreadsheet file and want to create a database insert script so you can process selected data from the spreadsheet in the database?
You need some or all values of a spreadsheet's columns to create formatted texts with these data?
Just describe what you would like to get in a text template file and let sheet2anything do the job.

## Installation

Download from https://github.com/georepl/sheet2anything.

## Usage

Syntax:

    $ java -jar sheet2anything-0.1.0-standalone.jar x <spreadsheed file> t <text template file>

Working example:




## Options

None yet.

## Examples

Assume your spreadsheet has a sheet called "Great Things" containing four columns A, B, C, and D. The data look like:

    A             B             C             D
    item          category      location      field
    Barrier Reef  reef          Coral Sea     geography
    Britain       country       Europe        politics
    Depression    mood          economy       economy
    Wall          wall          China         history
    White Shark   fish          oceans        biology


In order to feed these data into a database write a template describing the assignmant of spreadsheet data to variables in an insert script:


    §<SHEET "Great Things" { :A item :B category :C location :D field }>

    INSERT INTO mydb.GreatThings (gt_item,gt_category,gt_location,gt_field);
    VALUES ('§<item>','§<category>','§<location>', q'~§<field>~');


Furthermore assume you called the spreadsheet gt.xls and the template file insertDB.tpl.

Now call

      java -jar sheet2anything-0.1.0-standalone.jar x gt.xls t insertDB.tpl > insert.sql

and sheet2anything will create a file insert.sql containing this:


    INSERT INTO mydb.GreatThings (gt_item,gt_category,gt_location,gt_field);
    VALUES ('Barrier','Reef','reef', q'~Coral Sea~');

    INSERT INTO mydb.GreatThings (gt_item,gt_category,gt_location,gt_field);
    VALUES ('Britain','country','Europe', q'~geography~');

    INSERT INTO mydb.GreatThings (gt_item,gt_category,gt_location,gt_field);
    VALUES ('Depression','mood','economy', q'~politics~');

    INSERT INTO mydb.GreatThings (gt_item,gt_category,gt_location,gt_field);
    VALUES ('Wall','wall','China', q'~history~');

    INSERT INTO mydb.GreatThings (gt_item,gt_category,gt_location,gt_field);
    VALUES ('White Shark','fish','oceans', q'~biology~');


To use this file as database insert script delete the first INSERT statement because sheet2anything cannot (yet) ommit headlines.

### Bugs

No known bugs.

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2017 Thomas Neuhalfen

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
