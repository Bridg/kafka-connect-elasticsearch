/**
 * Copyright 2016 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 **/

package io.confluent.connect.elasticsearch;

import io.searchbox.action.BulkableAction;
import io.searchbox.core.Index;
import io.searchbox.core.Delete;

public class IndexableRecord {

  public final Key key;
  public final String payload;
  public final Long version;

  public IndexableRecord(Key key, String payload, Long version) {
    this.key = key;
    this.version = version;
    this.payload = payload;
  }

  public BulkableAction toBulkableAction() {
    // Null payload is treated as a tombstone and will delete from the index.
    if (this.payload == null) {
      return this.toDeleteRequest();
    } else {
      return this.toIndexRequest();
    }
  }

  public Index toIndexRequest() {
    Index.Builder req = new Index.Builder(payload)
        .index(key.index)
        .type(key.type)
        .id(key.id);
    if (version != null) {
      req.setParameter("version_type", "external").setParameter("version", version);
    }
    return req.build();
  }

  public Delete toDeleteRequest() {
    Delete.Builder req = new Delete.Builder(key.id)
        .index(key.index)
        .type(key.type);
    return req.build();
  }

}
