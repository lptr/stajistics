package org.stajistics.session.recorder;

/**
 * @author The Stajistics Project
 */
public class DistributionDataRecorderTest extends AbstractDataRecorderTestCase {

    @Override
    protected DataRecorder createDataRecorder() {
        return new DistributionDataRecorder();
    }
}
