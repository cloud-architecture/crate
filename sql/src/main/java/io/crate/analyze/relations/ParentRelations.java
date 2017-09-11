/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.analyze.relations;

import io.crate.sql.tree.QualifiedName;
import org.elasticsearch.common.collect.Tuple;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParentRelations {

    public static final ParentRelations NO_PARENTS = new ParentRelations();

    private final List<Map<QualifiedName, AnalyzedRelation>> sourcesTree;

    private ParentRelations() {
        sourcesTree = Collections.emptyList();
    }

    private ParentRelations(List<Map<QualifiedName, AnalyzedRelation>> sourcesTree) {
        this.sourcesTree = sourcesTree;
    }

    public ParentRelations newLevel(Map<QualifiedName, AnalyzedRelation> sources) {
        ArrayList<Map<QualifiedName, AnalyzedRelation>> newSourcesTree = new ArrayList<>(sourcesTree);
        newSourcesTree.add(sources);
        return new ParentRelations(newSourcesTree);
    }

    public boolean containsRelation(QualifiedName qualifiedName) {
        for (int i = sourcesTree.size() - 1; i >= 0; i--) {
            if (sourcesTree.get(i).containsKey(qualifiedName)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Tuple<Map<QualifiedName, AnalyzedRelation>, ParentRelations> pop() {
        if (sourcesTree.isEmpty()) {
            return null;
        }
        int size = sourcesTree.size();
        Map<QualifiedName, AnalyzedRelation> parentSources = sourcesTree.get(size - 1);
        return new Tuple<>(
            parentSources,
            new ParentRelations(sourcesTree.subList(0, size - 1))
        );
    }
}
