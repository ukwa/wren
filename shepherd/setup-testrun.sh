#!/bin/bash

# Set up a target
/python-shepherd/agents/w3act.py add-target --w3act-url $W3ACT_URL --w3act-user $W3ACT_USER --w3act-pw $W3ACT_PW "https://www.gov.uk/government/publications?departments[]=department-for-transport" "Department for Transport publications"

# Update the crawl schedule with a start date
/python-shepherd/agents/w3act.py update-schedule --w3act-url $W3ACT_URL --w3act-user $W3ACT_USER --w3act-pw $W3ACT_PW 1 daily "2016-01-13 09:00:00"

# Make it a watched Target
/python-shepherd/agents/w3act.py watch-target --w3act-url $W3ACT_URL --w3act-user $W3ACT_USER --w3act-pw $W3ACT_PW 1

# Add a Document (not needed here ATM)
#/python-shepherd/agents/w3act.py add-document --w3act-url $W3ACT_URL --w3act-user $W3ACT_USER --w3act-pw $W3ACT_PW 1 20160202235322 "https://www.gov.uk/government/uploads/system/uploads/attachment_data/file/492092/supplementary-guidance-january-2016.pdf" "https://www.gov.uk/government/publications/department-for-transport-delivers-more-grant-funding-to-transport-freight-by-rail"

# Launch
/python-shepherd/agents/launcher.py --w3act-url $W3ACT_URL --w3act-user $W3ACT_USER --w3act-pw $W3ACT_PW --amqp-url $AMQP_URL --timestamp "2016-01-13 09:00:00" uris-to-render

