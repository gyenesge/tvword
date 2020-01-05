insert
ignore into campaign_type
values ('T', 'Text campaign with background and text color.');
insert
ignore into campaign_type
values ('P', 'Picture campaign with a full screen picture.');
insert
ignore into user_role
values ('DISPLAY', 'Diplay user who can display campaigns');
insert
ignore into user_role
values ('ADMIN', 'Admin user, who can manage displays and campaigns.');
insert
ignore into status
values ('A', 'Active');
insert
ignore into status
values ('D', 'Deleted');