§<SHEET "Great Things" { :A item :B category :C location :D field }>

INSERT INTO mydb.GreatThings (gt_item,gt_category,gt_location,gt_field);
VALUES ('§<item>','§<category>','§<location>', q'~§<field>~');

