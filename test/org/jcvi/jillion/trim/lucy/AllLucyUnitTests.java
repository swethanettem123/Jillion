package org.jcvi.jillion.trim.lucy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestLucyLikeVectorSpliceTrimmer.class,
	TestLucyQualityTrimmer.class
})
public class AllLucyUnitTests {

}