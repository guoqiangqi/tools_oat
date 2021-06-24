/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ChangeLog:
 * 2021.5 -  Add new interface to support return the header matcher id.
 * Modified by jalenchen
 */

package ohos.oat.analysis.headermatcher;

import org.apache.rat.analysis.IHeaderMatcher;

/**
 * New oat matcher interface to support return the header matcher id.
 *
 * @author chenyaxun
 * @since 1.0
 */
public interface IOatHeaderMatcher extends IHeaderMatcher {

    String getMatcherId();

}
