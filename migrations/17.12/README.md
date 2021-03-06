# UPGRADING from 16.12 to 17.12

This is work-in-progress - please contribute to this documentation, by submitting pull requests.

## Database

In order to increase SQL query speed on the `ogcstatistics` schema, we added
several indexes to the database: one on the `user_name` and `date` columns for
all the tables located in the `ogcstatics.ogc_services_log` partition, except
for the last one. The last table receives new inserts, so adding an index there
would lower performances.

To automatically create these new indexes, you should update the
`get_partition_table()` procedure, using the `update-ogc-server-statistics.sql`
file.

For tables that were already present in your database, you should manually index
them, using the following queries. You have to adapt table names based on the
current state of your database.

For example, to create indexes on the table that holds stats for october 2016:
```sql
CREATE INDEX ogc_services_log_y2016m10_user_name_idx ON ogcstatistics.ogc_services_log_y2016m10 (user_name);
CREATE INDEX ogc_services_log_y2016m10_date_idx ON ogcstatistics.ogc_services_log_y2016m10 (date);
```

You should create these indexes for all tables except for the current month.

## Sources

Due to submodule changes, upgrading your sources repository involves the following commands:
```
git fetch origin
git checkout 17.12
git submodule sync --recursive
git submodule update --init --recursive --force
```

## GeoServer - Notes for tomcat users

the default Context attribute `useRelativeRedirects` seems to have changed from
`false` to `true` in the new versions of Tomcat, making the redirections from
the GeoServer UI fail.

You will have to manually adapt your tomcat configuration to add the following in your `context.xml` file:

```xml
<Context useRelativeRedirects="false">
```

Forgetting to adjust your configuration will result in a unusable GeoServer UI.
The first version of the HTTP specicifications required an absolute URL to be
passed to the `Location` header, but since most browsers tolerate a relative
URL, this constraint has been relaxed in the new version of the specifications.

Nevertheless we preferred to keep the same behaviour across the servlet
containers used in the geOrchestra community (mainly jetty and tomcat), and we
actually require an absolute URL so that the Security-Proxy is able to rewrite
these to point to a coherent destination.

