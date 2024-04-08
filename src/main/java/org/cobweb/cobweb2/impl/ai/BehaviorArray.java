package org.cobweb.cobweb2.impl.ai;

import java.util.Arrays;
import java.util.Random;

import org.cobweb.util.BitField;
import org.cobweb.util.RandomNoGenerator;

/**
 * BehaviorArray is a class designed to take a collection of bits as input and for each permutation of those bits,
 * return a set of corresponding output bits. A set of output bits are stored for each permutation and thus the size of
 * this array grow exponentially with the number of input bits.
 *
 * Input: A collection of 6 bit groups combined into a single number:
 * <ol>
 * <li> 2 bits for first "seen" object (nothing, agent, food, barrier) </li>
 * <li> 2 bits for distance of "seen" object </li>
 * <li> n bits for memory input (n equal to mem output from previous timestep) </li>
 * <li> m bits for communication code received (m equal to communication from output) </li>
 * <li> 2 bits for orientation (N(00),S(01),E(10),W(11)) </li>
 * <li> 2 bits for agent's current energy level == floor(E/40) and no more than 3 </li>
 * </ol>
 *
 * Output: A collection of 4 outputs:
 * <ol>
 * <li> 2 bits for physical action (0-left, 1-right, 2-stop, 3-step) </li>
 * <li> n bits for memory output (n is user specified) </li>
 * <li> m bits for communication w/ other agents </li>
 * <li> 1 bit for sexual(0)/asexual(1)reproduction </li>
 * </ol>
 *
 * In addition to associating a group of output bits with each permutation of input bits, BehaviorArray splits the
 * output into functional pieces, and during mutation and similarity operations it examines these pieces individually.
 *
 * BehaviorArray is extremely efficient in its storage of bits. It uses an array of integers but no single bit is
 * wasted. The amount of memory it consumes is approximately: outputBits*2^(inputBits-3) bytes
 *
 */

public class BehaviorArray {

	public final int[] outputSize;

	public final int inputSize;
	/**
	 * Mask for all output bits in the form 0, 1, 2.., inputSize - 1
	 */
	private final int totalInMask;
	/**
	 * Mask for all output bits in the form 0, 1, 2.., outputBits - 1
	 */
	private final int totalOutMask;

	private int outputBits;

	/**
	 * totalInts is equal to the totalbits divided by 32 (there's 32 bits in an int) plus 1 since we might have a
	 * partial int used and the shift operation will round down. Certain methods also assume the existance of this
	 * extra int
	 */
	private int totalInts;

	private final int[] array;
	/**
	 * number of elements is the number of permutations for the input bits
	 */
	private final int size;

	/**
	 * totalBits is equal to the output size times the number of permutations for the input bits
	 */
	private final int totalBits;

	/**
	 * Constructor: Creates a behavior array that can accept an amount of input bits specified by the first parameter,
	 * and stores groups of output bits who's number and individual sizes are specified by the second parameter.
	 */
	public BehaviorArray(int input, int[] output) {

		/*
		 * Save basic information
		 */
		inputSize = input;
		outputSize = output;
		outputBits = 0;

		for (int i = 0; i < outputSize.length; ++i) {
			outputBits += outputSize[i];
		}

		if (inputSize > 32) {
			throw new java.lang.IllegalArgumentException("inputSize exceded 32");
		}

		if (outputBits > 32) {
			throw new java.lang.IllegalArgumentException("outputBits exceded 32");
		}


		totalOutMask = (1 << (outputBits)) - 1;
		totalInMask = (1 << (inputSize)) - 1;
		size = (1 << (inputSize));
		totalBits = outputBits * size;
		totalInts = (totalBits >> 5) + 1;

		if (totalInts == 1) {
			totalInts = 2;
		}

		array = new int[totalInts];
	}

	private BehaviorArray(BehaviorArray original) {
		this.inputSize = original.inputSize;
		this.outputSize = original.outputSize; // Ok to copy reference, it doesn't change
		this.outputBits = original.outputBits;
		this.totalOutMask = original.totalOutMask;
		this.totalInMask = original.totalInMask;
		this.size = original.size;
		this.totalBits = original.totalBits;
		this.totalInts = original.totalInts;
		this.array = Arrays.copyOf(original.array, original.array.length);
	}

	/**
	 * Creates copy of BehaviorArray with mutationRate fraction of bits inverted
	 * @param mutationRate fraction of bits to invert
	 * @param rand random number source
	 * @return mutated copy of BehaviorArray
	 */
	public BehaviorArray copy(float mutationRate, Random rand) {
		BehaviorArray newArray = new BehaviorArray(this);

		double avgFlips = newArray.totalBits * mutationRate;
		int wantFlips = (int) (rand.nextGaussian() * 0.1 * avgFlips + avgFlips);
		if (wantFlips < 0)
			wantFlips = 0;

		for (int i = 0; i < wantFlips; i++) {
			int target = rand.nextInt(newArray.totalBits);
			int targetElement = target / 32;
			int targetBit = target % 32;
			newArray.array[targetElement] ^= (1 << targetBit);
		}

		return newArray;
	}

	int get(int index) {

		/* bitbase is the first bit of the target element */
		int bitbase = index * outputBits;
		/*
		 * base is the first int value of the array that contains bit(s) of the target element
		 */
		int base = bitbase / 32;

		/*
		 * basemod is the number of the first bit of the target element in the int value
		 */
		int basemod = bitbase % 32;

		/*
		 * Extracts the element value from the appropriate two array elements.
		 */

		long buff = (array[base] & 0xFFFFFFFFL) | ((long) array[base + 1] << 32);
		return (int) ((buff >>> basemod) & totalOutMask);
	}

	void mutateOutput(int index, float strength, Random rand) {
		int value = get(index);
		int newValue = value;

		for (int i = 0; i < outputBits; i++) {
			int b = 1 << i;
			if (rand.nextFloat() < strength)
				newValue ^= b;
		}

		set(index, newValue);
	}

	/**
	 * @return an array filled with the different outputs associated with the element number given as a parameter
	 */
	public int[] getOutput(int in) {
		int[] out = new int[outputSize.length];
		BitField outputCode = new BitField(get(in & totalInMask));

		for (int i = 0; i < outputSize.length; ++i) {
			out[i] = outputCode.remove(outputSize[i]);
		}

		return out;
	}

	public int getSize() {
		return size;
	}

	/**
	 * fills the array with random bits one integer at a time
	 * @param seed Random seed to use
	 */
	public void randomInit(long seed) {

		RandomNoGenerator rng = new RandomNoGenerator(seed);
		for (int i = 0; i < totalInts; ++i) {
			array[i] = rng.nextInt();
		}

	}

	void set(int index, int value) {

		/* bitbase is the first bit of the target element */
		int bitbase = index * outputBits;
		/*
		 * base is the first int value of the array that contains bit(s) of the target element
		 */
		int base = bitbase / 32;
		/*
		 * basemod is the number of the first bit of the target element in the int value
		 */
		int basemod = bitbase % 32;
		/* mask off any excess bits */
		value &= totalInMask;

		/*
		 * Stores the element value to the appropriate two array elements.
		 */
		long buff = (array[base] & 0xFFFFFFFFL) | ((long) array[base + 1] << 32);

		buff &= ~((long) totalOutMask << basemod);
		buff |= (long) value << basemod;

		array[base] = (int) buff;
		array[base + 1] = (int) (buff >>> 32);
	}

	public double similarity(BehaviorArray other) {

		double total = 0;
		int cmpsize = size;
		if (size > other.getSize()) {
			cmpsize = other.getSize();
		}

		for (int i = 0; i < cmpsize; ++i) {
			BitField outputCode1 = new BitField(get(i));
			BitField outputCode2 = new BitField(other.get(i));
			for (int j = 0; j < outputSize.length; ++j) {
				if (outputCode1.remove(outputSize[j]) == outputCode2.remove(outputSize[j])) {
					total += outputSize[j];
				}
			}
		}
		return total / (size * outputBits);
	}

	/**
	 * Splice takes another behavior array as a parameter and genetically splices it with this one to create a new
	 * behavior array. The algorithm is random and ensures that exactly half of the genetic makeup of each parent is
	 * used.
	 *
	 * Preconditions: BOther must be identical to this one in terms of input bits and output bits.
	 *
	 * @return a new behavior that is a splice of this one and the parameter
	 */

	public static BehaviorArray splice(BehaviorArray first, BehaviorArray BOther, Random random) {
		// Copy first array
		BehaviorArray newBArray = new BehaviorArray(first);

		// Take elements from BOther with 50% probability
		for (int i = 0; i < first.size; ++i) {
			newBArray.set(i, random.nextBoolean() ? BOther.get(i) : newBArray.get(i));
		}

		return newBArray;
	}

}
