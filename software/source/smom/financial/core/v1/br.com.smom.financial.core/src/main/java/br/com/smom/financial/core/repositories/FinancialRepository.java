/*
 * Smom - Software Module Management.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.smom.financial.core.repositories;

import br.com.smom.customer.api.exceptions.CustomerException;
import br.com.smom.customer.api.services.Customer;
import br.com.smom.financial.api.enums.FinancialMessages;
import br.com.smom.financial.api.exceptions.FinancialException;
import br.com.smom.financial.api.model.entities.FinancialEntity;
import br.com.smom.financial.core.dao.IAccountDAO;
import br.com.smom.financial.core.dao.IFinancialDAO;
import br.com.smom.financial.core.dao.IPaymentTypeDAO;
import br.com.smom.log.api.services.Log;
import br.com.smom.main.datasource.api.exceptions.DataSourceException;
import br.com.smom.main.datasource.api.services.PostgreSQL;
import br.com.smom.main.util.api.services.ServiceProvider;
import java.sql.Connection;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class FinancialRepository implements IFinancialRepository {

    private PostgreSQL postgreSQLService = null;
    private Log logService = null;
    private Customer customerService = null;

    @Inject
    private IFinancialDAO financialDAO;
    @Inject
    private IAccountDAO accountDAO;
    @Inject
    private IPaymentTypeDAO paymentTypeDAO;

    @Override
    public FinancialEntity create(FinancialEntity financialEntity) throws FinancialException {
        postgreSQLService = (PostgreSQL) ServiceProvider.getBundleService(PostgreSQL.class);
        logService = (Log) ServiceProvider.getBundleService(Log.class);

        FinancialEntity financialCreated;
        Connection connection = null;

        if (postgreSQLService != null) {
            try {
                connection = postgreSQLService.getConnection();
                financialDAO.setConnection(connection);
                financialCreated = financialDAO.create(financialEntity);
                
                financialCreated = fillFinancialRelease(financialCreated);
                postgreSQLService.commit(connection);

                if (logService != null) {
                    logService.info("Financial created: " + financialCreated.toString());
                }
                return financialCreated;
            } catch (DataSourceException e) {
                postgreSQLService.rollback(connection);
                throw new FinancialException(e);
            }
        } else {
            if (logService != null) {
                logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("PostgreSQL Service is null"));
            }
            throw new FinancialException(FinancialMessages.WARN_UNAVAILABLE_MODULE);
        }
    }

    @Override
    public FinancialEntity update(FinancialEntity financialEntity) throws FinancialException {
        postgreSQLService = (PostgreSQL) ServiceProvider.getBundleService(PostgreSQL.class);
        logService = (Log) ServiceProvider.getBundleService(Log.class);

        FinancialEntity financialUpdated;
        Connection connection = null;
        int customerId;

        if (postgreSQLService != null) {
            try {
                connection = postgreSQLService.getConnection();
                customerId = financialEntity.getId();
                financialDAO.setConnection(connection);
                financialDAO.update(financialEntity);
                financialUpdated = financialDAO.getById(customerId);

                postgreSQLService.commit(connection);

                if (logService != null) {
                    logService.info("Financial updated: " + financialUpdated.toString());
                }
                financialUpdated = fillFinancialRelease(financialUpdated);
                return financialUpdated;
            } catch (DataSourceException e) {
                postgreSQLService.rollback(connection);
                throw new FinancialException(e);
            }
        } else {
            if (logService != null) {
                logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("PostgreSQL Service is null"));
            }
            throw new FinancialException(FinancialMessages.WARN_UNAVAILABLE_MODULE);
        }
    }

    @Override
    public void delete(FinancialEntity financialEntity) throws FinancialException {
        postgreSQLService = (PostgreSQL) ServiceProvider.getBundleService(PostgreSQL.class);
        logService = (Log) ServiceProvider.getBundleService(Log.class);

        Connection connection = null;

        if (postgreSQLService != null) {
            try {
                connection = postgreSQLService.getConnection();
                financialDAO.setConnection(connection);
                financialDAO.delete(financialEntity);
                postgreSQLService.commit(connection);

                if (logService != null) {
                    logService.info("Financial deleted: " + financialEntity.toString());
                }
            } catch (DataSourceException e) {
                postgreSQLService.rollback(connection);
                throw new FinancialException(e);
            }
        } else {
            if (logService != null) {
                logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("PostgreSQL Service is null"));
            }
            throw new FinancialException(FinancialMessages.WARN_UNAVAILABLE_MODULE);
        }
    }

    @Override
    public FinancialEntity getById(int id) throws FinancialException {
        postgreSQLService = (PostgreSQL) ServiceProvider.getBundleService(PostgreSQL.class);
        logService = (Log) ServiceProvider.getBundleService(Log.class);

        FinancialEntity financialEntity;
        Connection connection = null;

        if (postgreSQLService != null) {
            try {
                connection = postgreSQLService.getConnection();
                financialDAO.setConnection(connection);
                financialEntity = financialDAO.getById(id);
                postgreSQLService.commit(connection);
                
                financialEntity = fillFinancialRelease(financialEntity);
                if (logService != null) {
                    logService.info("Financial getting: " + (financialEntity != null ? financialEntity.toString() : "is null"));
                }
                return financialEntity;
            } catch (DataSourceException e) {
                postgreSQLService.rollback(connection);
                throw new FinancialException(e);
            }
        } else {
            if (logService != null) {
                logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("PostgreSQL Service is null"));
            }
            throw new FinancialException(FinancialMessages.WARN_UNAVAILABLE_MODULE);
        }
    }

    @Override
    public List<FinancialEntity> getByPeople(int id) throws FinancialException {
        postgreSQLService = (PostgreSQL) ServiceProvider.getBundleService(PostgreSQL.class);
        logService = (Log) ServiceProvider.getBundleService(Log.class);

        List<FinancialEntity> financialEntityList;
        Connection connection = null;

        if (postgreSQLService != null) {
            try {
                connection = postgreSQLService.getConnection();

                financialDAO.setConnection(connection);
                financialEntityList = financialDAO.getByPeople(id);
                
                postgreSQLService.commit(connection);
                if (logService != null) {
                    logService.info("Financial getting: " + (financialEntityList != null ? financialEntityList.toString() : "is null"));
                }
                return financialEntityList;
            } catch (DataSourceException e) {
                postgreSQLService.rollback(connection);
                throw new FinancialException(e);
            }
        } else {
            if (logService != null) {
                logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("PostgreSQL Service is null"));
            }
            throw new FinancialException(FinancialMessages.WARN_UNAVAILABLE_MODULE);
        }
    }

    @Override
    public List<FinancialEntity> getByCreateDate(String startDate, String endDate) throws FinancialException {
        postgreSQLService = (PostgreSQL) ServiceProvider.getBundleService(PostgreSQL.class);
        logService = (Log) ServiceProvider.getBundleService(Log.class);

        List<FinancialEntity> financialEntityList;
        Connection connection = null;

        if (postgreSQLService != null) {
            try {
                connection = postgreSQLService.getConnection();

                financialDAO.setConnection(connection);
                financialEntityList = financialDAO.getByCreateDate(startDate, endDate);
                postgreSQLService.commit(connection);
                
                for (FinancialEntity fe : financialEntityList) {
                    fe = fillFinancialRelease(fe);
                }
                
                if (logService != null) {
                    logService.info("Financial getting: " + (financialEntityList != null ? financialEntityList.toString() : "is null"));
                }
                return financialEntityList;
            } catch (DataSourceException e) {
                postgreSQLService.rollback(connection);
                throw new FinancialException(e);
            }
        } else {
            if (logService != null) {
                logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("PostgreSQL Service is null"));
            }
            throw new FinancialException(FinancialMessages.WARN_UNAVAILABLE_MODULE);
        }
    }

    @Override
    public List<FinancialEntity> getByDueDate(String startDate, String endDate) throws FinancialException {
        postgreSQLService = (PostgreSQL) ServiceProvider.getBundleService(PostgreSQL.class);
        logService = (Log) ServiceProvider.getBundleService(Log.class);

        List<FinancialEntity> financialEntityList;
        Connection connection = null;

        if (postgreSQLService != null) {
            try {
                connection = postgreSQLService.getConnection();

                financialDAO.setConnection(connection);
                financialEntityList = financialDAO.getByDueDate(startDate, endDate);
                postgreSQLService.commit(connection);
                
                for (FinancialEntity fe : financialEntityList) {
                    fe = fillFinancialRelease(fe);
                }
                
                if (logService != null) {
                    logService.info("Financial getting: " + (financialEntityList != null ? financialEntityList.toString() : "is null"));
                }
                return financialEntityList;
            } catch (DataSourceException e) {
                postgreSQLService.rollback(connection);
                throw new FinancialException(e);
            }
        } else {
            if (logService != null) {
                logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("PostgreSQL Service is null"));
            }
            throw new FinancialException(FinancialMessages.WARN_UNAVAILABLE_MODULE);
        }
    }

    private FinancialEntity fillFinancialRelease(FinancialEntity financialEntity) throws FinancialException{
        Connection connection = null;
        postgreSQLService = (PostgreSQL) ServiceProvider.getBundleService(PostgreSQL.class);
        logService = (Log) ServiceProvider.getBundleService(Log.class);
        customerService = (Customer) ServiceProvider.getBundleService(Customer.class);

        if (postgreSQLService != null) {
            try {
                connection = postgreSQLService.getConnection();

                accountDAO.setConnection(connection);
                financialEntity.setAccount(accountDAO.getById(financialEntity.getAccountId()));

                paymentTypeDAO.setConnection(connection);
                financialEntity.setPaymentType(paymentTypeDAO.getById(financialEntity.getPaymentTypeId()));

                if (customerService != null) {
                    financialEntity.setPeople(customerService.getById(financialEntity.getPeopleId()));
                } else {
                    if (logService != null) {
                        logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("Customer Service is null"));
                    }
                }

                if (logService != null) {
                    logService.info("Financial created: " + financialEntity.toString());
                }
                return financialEntity;
            } catch (DataSourceException e) {
                postgreSQLService.rollback(connection);
                throw new FinancialException(e);
            } catch (CustomerException ex) {
                throw new FinancialException(ex);
            }
        } else {
            if (logService != null) {
                logService.warn(FinancialMessages.WARN_UNAVAILABLE_MODULE.getMessage("PostgreSQL Service is null"));
            }
            throw new FinancialException(FinancialMessages.WARN_UNAVAILABLE_MODULE);
        }

    }

}