/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.analysis;

import java.util.List;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Observer for the {@link ObservableAnalysis}, will be notified after all analysis results are available.
 * 
 * @author El-Sharkawy
 *
 */
public interface IAnalysisObserver {
    
    /**
     * Will be called after the last result was produced.
     * 
     * @param analysisResults Contains all produced results, the list will be of type of the input/output types of
     *     the observed analysis
     */
    public void notifyFinished(@NonNull List<@NonNull ?> analysisResults);

    /**
     * Notifies that the analysis has come to an (unexpected) end and has produced no results.
     */
    public void notifyFinished();
}
