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
 * BaseRepository implements a Chain of responsibility
 * basic services
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class BaseRepository<K, V extends RepoElement<K>>
        implements Repository<K, V> {

    /**
     * Next Level services
     */
    protected final Repository<K, V> nextLevel;

    /**
     * Creates a new instance of a BaseRepository with next Level
     *
     * @param nextLevel Next Level of the Repository
     */
    BaseRepository(Repository<K, V> nextLevel) {
        this.nextLevel = nextLevel;
    }

    /**
     * Checks if the current repository has next level
     *
     * @return TRUE if affirmative, FALSE otherwise
     */
    protected boolean hasNextLevel() {
        return nextLevel != null && nextLevel.isAvailable();
    }
}
