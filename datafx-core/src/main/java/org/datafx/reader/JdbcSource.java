/**
 * Copyright (c) 2011, 2013, Jonathan Giles, Johan Vos, Hendrik Ebbers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of DataFX, the website javafxdata.org, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.datafx.reader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.datafx.reader.converter.JdbcConverter;
import org.datafx.reader.converter.JdbcDataSourceUtil;

/**
 *
 * @author johan
 */
public class JdbcSource<T> extends AbstractDataReader<T> implements WritableDataReader<T> {

    private final String jdbcUrl;
    private final String sqlStatement;
    private final JdbcConverter<T> converter;
    private boolean connectionCreated;
    private boolean lastResult;
    private ResultSet resultSet;
    private boolean updateQuery = false;

    public JdbcSource(String jdbcUrl, JdbcConverter<T> converter, String table, String... cols) {
        this(jdbcUrl, JdbcDataSourceUtil.createSelectStatement(table, cols), converter);
    }

    public JdbcSource(String jdbcUrl, String selectStatement,
            JdbcConverter<T> converter) {
        this.jdbcUrl = jdbcUrl;
        this.sqlStatement = selectStatement;
        this.converter = converter;
    }
    
    public void setUpdateQuery (boolean b) {
        this.updateQuery = b;
    }

    private synchronized void createConnection() {
        if (connectionCreated) {
            return;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl);
            Statement query;
            if (updateQuery) {
                System.out.println("updatequery");
                query = connection.createStatement();
            } else {
                System.out.println("regularquery");
                query = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
            }
            if (updateQuery) {
                query.executeUpdate(sqlStatement);
            }
            else {
            resultSet = query.executeQuery(sqlStatement);
            lastResult = resultSet.next();
            converter.initialize(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectionCreated = true;
    }

    @Override
    public void writeBack() {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);
            Statement query = connection.createStatement();
            query.executeUpdate(sqlStatement);
        } catch (SQLException ex) {
            Logger.getLogger(JdbcSource.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public T get() {
        // TODO: refactor this. We call this method on updates as well, while we don't need to parse a result on updates.
        if (!connectionCreated) {
            createConnection();
        }
        if (converter != null) {
            return converter.get();
        }
        else {
            return null;
        }
    }

    @Override
    public boolean next() {
        if (!connectionCreated) {
            createConnection();
        }
        boolean answer = converter.next();
        return answer;
    }
}
