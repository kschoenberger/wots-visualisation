package files;

import files.Function;

/**
 * @author Moritz Horsch <horsch@cdc.informatik.tu-darmstadt.de>
 */
public abstract class PseudorandomFunction extends Function {

    /**
     * Creates a new PseudorandomFunction.
     *
     * @param oid Object identifier
     */
    protected PseudorandomFunction(String oid) {
	super(oid);
    }

    /**
     * Applies the function.
     *
     * @param input Input
     * @param key Key
     * @return
     */
    public abstract byte[] apply(byte[] input, byte[] key);
}
