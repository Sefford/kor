/*
 * Copyright (C) 2014 Saúl Díaz
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
package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;

/**
 * BaseRepository implements a Chain of Responsibility pattern for the Repositories.
 * <p/>
 * In order to achieve true interchangeable cache levels, the default repositories are built upon
 * a chain of responibility pattern, so the updates can happen sequentially on the repository hierarchy.
 * <p/>
 * While the default implementation is based around this hierarchial organization of repositories,
 * both requests and repositories does not enforce this requirement, and unrelated - standalone
 * repos can be created through {@link com.sefford.kor.repositories.interfaces.Repository Repository Interface}
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class BaseRepository<K, V extends RepoElement<K>>
        implements Repository<K, V> {

    /**
     * Next level of the repository if any.
     */
    protected final Repository<K, V> nextLevel;

    /**
     * Creates a new instance of a BaseRepository with next level.
     *
     * This next level can be optionally initialized to null.
     *
     * @param nextLevel Next Level of the Repository
     */
    protected BaseRepository(Repository<K, V> nextLevel) {
        this.nextLevel = nextLevel;
    }

    /**
     * Checks if the current repository has next level.
     *
     * @return TRUE if affirmative, FALSE otherwise
     */
    protected boolean hasNextLevel() {
        return nextLevel != null && nextLevel.isAvailable();
    }
}
