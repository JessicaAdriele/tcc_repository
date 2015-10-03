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
package br.com.smom.customer.core.dao;

import br.com.smom.customer.api.model.entities.AddressEntity;
import br.com.smom.main.util.api.exceptions.UtilException;
import java.sql.Connection;
import java.util.List;

public interface IAddressDAO {
    
    public void setConnection(Connection connection);

    public int create(AddressEntity addressEntity) throws UtilException;

    public void update(AddressEntity addressEntity) throws UtilException;

    public void delete(AddressEntity addressEntity) throws UtilException;

    public AddressEntity getById(int id) throws UtilException;
    
    public List<AddressEntity> getByCustomerId(int customerId) throws UtilException;
    
}
