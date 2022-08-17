--
--  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       Mercedes-Benz Tech Innovation GmbH - EDC Snapshot 20220815 Update
--

-- add columns
ALTER TABLE edc_transfer_process ADD COLUMN updated_at BIGINT;

-- rename columns
-- the EDC specifies this colum as NOT NULL, but for the migration it will stay NULLABLE
ALTER TABLE edc_transfer_process RENAME COLUMN created_time_stamp TO created_at;