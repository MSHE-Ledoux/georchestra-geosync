package org.georchestra.console.dao;

import org.georchestra.console.model.DelegationEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Delegation2Dao {

    @Autowired
    private JpaTransactionManager tm;

    private PreparedStatement byOrgStatement;

    @PostConstruct
    public void init() throws SQLException {
        this.byOrgStatement = this.tm.getDataSource().getConnection().prepareStatement(
                "SELECT uid, array_to_string(orgs, ',') AS orgs, array_to_string(roles, ',') AS roles FROM console.delegations WHERE ? = ANY(orgs)");
    }

    public List<DelegationEntry> findByOrg(String org) throws SQLException {

        List<DelegationEntry> res = new LinkedList<DelegationEntry>();
        this.byOrgStatement.setString(1,org);
        ResultSet rawRes = this.byOrgStatement.executeQuery();
        while(rawRes.next()) {
            DelegationEntry de = new DelegationEntry(rawRes.getString("uid"),
                    rawRes.getString("orgs").split(","),
                    rawRes.getString("roles").split(","));
            res.add(de);
        }

        return res;
    }

}