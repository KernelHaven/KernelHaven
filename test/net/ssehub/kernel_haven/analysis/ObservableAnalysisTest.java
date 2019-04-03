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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import net.ssehub.kernel_haven.test_utils.AnalysisComponentExecuter;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the {@link ObservableAnalysis}.
 *
 * @author Adam
 */
public class ObservableAnalysisTest implements IAnalysisObserver {

    private List<@NonNull ?> results;
    
    private boolean notifyCalled;
    
    /**
     * Tests whether an observer is notified about results.
     * 
     * @throws InterruptedException unwanted.
     */
    @Test(timeout = 5000)
    public void testWithResults() throws InterruptedException {
        ObservableAnalysis.setObservers(this);
        
        AnalysisComponentExecuter.executeComponent(ObservableAnalysis.class, null, new String[] {"a", "b", "c"});
            
        synchronized (this) {
            if (!notifyCalled) {
                wait(); // wait until notify was called
            }
        }
        
        assertThat(notifyCalled, is(true));
        assertThat(results, is(Arrays.asList("a", "b", "c")));
    }
    
    /**
     * Tests whether an observer is notified if no results have been produced.
     * 
     * @throws InterruptedException unwanted.
     */
    @Test(timeout = 5000)
    public void testWitoutResults() throws InterruptedException {
        ObservableAnalysis.setObservers(this);
        
        AnalysisComponentExecuter.executeComponent(ObservableAnalysis.class, null, new Object[0]);
            
        synchronized (this) {
            if (!notifyCalled) {
                wait(); // wait until notify was called
            }
        }
        
        assertThat(notifyCalled, is(true));
        assertThat(results, nullValue());
    }

    @Override
    public void notifyFinished(@NonNull List<@NonNull ?> analysisResults) {
        this.results = analysisResults;
        synchronized (this) {
            notifyCalled = true;
            notify();
        }
    }

    @Override
    public void notifyFinished() {
        synchronized (this) {
            notifyCalled = true;
            notify();
        }
    }
    
}
