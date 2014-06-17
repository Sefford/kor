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
package com.sefford.kor.common.interfaces;

/**
 * Postable interface done to abstract a notification interface from the Renderers.
 * <p/>
 * This is done to separate the notification system from the renderer. Typically this is intended
 * to use it straightforward with an Event Bus as <a href="GreenRobot's">https://github.com/greenrobot/EventBus</a> or
 * <a href="Square's Otto">http://square.github.io/otto/</a>.
 * <p/>
 * In this way the developer will be able to notify of UI events from the Renderer to upper layers of
 * the UI.
 *
 * @author <sefford@gmail.com>
 */
public interface Postable {

    /**
     * Posts an object to the notification manager.
     *
     * @param event Event to send through the notification system.
     */
    void post(Object event);
}