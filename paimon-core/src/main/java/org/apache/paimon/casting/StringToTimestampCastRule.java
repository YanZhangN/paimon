/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.casting;

import org.apache.paimon.data.BinaryString;
import org.apache.paimon.data.Timestamp;
import org.apache.paimon.types.DataType;
import org.apache.paimon.types.DataTypeChecks;
import org.apache.paimon.types.DataTypeFamily;
import org.apache.paimon.types.DataTypeRoot;
import org.apache.paimon.utils.BinaryStringUtils;

import java.util.TimeZone;

/** {@link DataTypeFamily#CHARACTER_STRING} to {@link DataTypeFamily#TIMESTAMP} cast rule. */
class StringToTimestampCastRule extends AbstractCastRule<BinaryString, Timestamp> {

    static final StringToTimestampCastRule INSTANCE = new StringToTimestampCastRule();

    private StringToTimestampCastRule() {
        super(
                CastRulePredicate.builder()
                        .input(DataTypeFamily.CHARACTER_STRING)
                        .target(DataTypeFamily.TIMESTAMP)
                        .build());
    }

    @Override
    public CastExecutor<BinaryString, Timestamp> create(DataType inputType, DataType targetType) {
        int targetPrecision = DataTypeChecks.getPrecision(targetType);
        if (targetType.is(DataTypeRoot.TIMESTAMP_WITHOUT_TIME_ZONE)) {
            return value -> BinaryStringUtils.toTimestamp(value, targetPrecision);
        } else if (targetType.is(DataTypeRoot.TIMESTAMP_WITH_LOCAL_TIME_ZONE)) {
            return value ->
                    BinaryStringUtils.toTimestamp(value, targetPrecision, TimeZone.getDefault());
        }
        return null;
    }
}
