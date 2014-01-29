//package team144.rolit.tests;
//
//
//public abstract class UnitTest implements Runnable{
//
//
//    /** Number of errors. */
//    private int errors;
//    /** Notice belonging to test method. */
//    private boolean isPrinted;
//    /** Indication that an errors was found in test method. */
//    private String description;
//
//    /** Calls all tests in this class one-by-one. */
//    public void run(){
//        if (errors == 0) {
//            System.out.println("    OK");
//        }
//        return errors;
//    }
//
//    /**
//     * Sets the instance variable <tt>hotel</tt> to a well-defined initial value.
//     * All test methods should be preceded by a call to this method.
//     */
//    protected abstract void setUp();
//
//    /**
//     * Fixes the status for the testmethod's description.
//     * @param text The description to be printed
//     */
//    protected void beginTest(String text) {
//        description = text;
//        // the description hasn't been printed yet
//        isPrinted = false;
//        run();
//    }
//
//    /**
//     * Tests if the resulting value of a tested expression equals the 
//     * expected (correct) value. This implementation prints both values, 
//     * with an indication of what was tested, to the standard output. The 
//     * implementation does not actually do the comparison.
//     */
//    private void assertEquals(String text, Object expected, Object result) {
//        boolean equal;
//        // tests equality between expected and result
//        // accounting for null
//        if (expected == null) {
//            equal = result == null;
//        } else {
//            equal = result != null && expected.equals(result);
//        }
//        if (!equal) {
//            // prints the description if necessary
//            if (!isPrinted) {
//                System.out.println("    Test: " + description);
//                // now the description is printed
//                isPrinted = true;
//            }
//            System.out.println("        " + text);
//            System.out.println("            Expected:  " + expected);
//            System.out.println("            Result: " + result);
//            errors++;
//        }
//    }
//    
//}
