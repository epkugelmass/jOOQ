/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.impl;

import static org.jooq.impl.Tools.castIfNeeded;

import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.QueryPart;


/**
 * @author Lukas Eder
 */
final class DateOrTime<T> extends AbstractFunction<T> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -6729613078727690134L;

    private final Field<?>    field;

    DateOrTime(Field<?> field, DataType<T> dataType) {
        super(name(dataType), dataType, field);

        this.field = field;
    }

    private static String name(DataType<?> dataType) {
        return dataType.isDate()
             ? "date"
             : dataType.isTime()
             ? "time"
             : "timestamp";
    }

    @Override
    final QueryPart getFunction0(Configuration configuration) {
        switch (configuration.family()) {




            case MYSQL:
            case MARIADB:
                return DSL.field("{" + name(getDataType()) + "}({0})", getDataType(), field);

            case SQLITE: {
                if (getDataType().isDate())
                    return DSL.field("{date}({0})", getDataType(), field);
                else if (getDataType().isTime())
                    // [#8733] No fractional seconds for time literals
                    return DSL.field("{time}({0})", getDataType(), field);
                else
                    return DSL.field("{strftime}('%Y-%m-%d %H:%M:%f', {0})", getDataType(), field);
            }

            default:
                return castIfNeeded(field, getDataType());
        }
    }
}
