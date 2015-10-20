# pizza-ldap-muc

LDAP-authenticated MUC server for use with pizza-auth

## Overview

This service allows you to run an external Multi User Conference server that can be hooked up to your existing prosody/ejabberd/openfire server.

When a user attempts to join a channel on this conference server, it will check to see if they are in the same named auth group, and disallow access if they aren't.

I recommend you deploy this as a conference.groups.your.tld server and keep it separate from your main conference.your.tld server.o

## Building

Download a new-ish openfire and copy it's lib folder in here, then build with sbt.