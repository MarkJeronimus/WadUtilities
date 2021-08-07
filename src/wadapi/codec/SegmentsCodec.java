package wadapi.codec;

import org.digitalmodular.utilities.annotation.Singleton;

import wadapi.FileBuffer;
import wadapi.LumpUtilities;
import wadapi.lump.FileBufferLump;
import wadapi.lump.SegmentsLump;
import wadapi.structure.Segment;

/**
 * Generated by node builders
 *
 * @author Zom-B
 */
// Created 2011-08-15
@Singleton
public class SegmentsCodec extends LumpCodec<SegmentsLump> {
	public static final int SEGMENT_FIELD_SIZE = 12;

	public static final SegmentsCodec INSTANCE = new SegmentsCodec();

	@Override
	public SegmentsLump decode(FileBufferLump lump) {
		String     name       = lump.getName();
		FileBuffer fileBuffer = lump.getFileBuffer();

		int numSegments = LumpUtilities.calcNumFields(fileBuffer.remaining(), SEGMENT_FIELD_SIZE, name);

		SegmentsLump segmentsLump = new SegmentsLump(name, numSegments);

		for (int i = 0; i < numSegments; i++) {
			Segment segment = readSegment(fileBuffer);
			segmentsLump.add(segment);
		}

		return segmentsLump;
	}

	@Override
	public void encode(SegmentsLump segmentsLump, FileBuffer buffer) {
		throw new UnsupportedOperationException("Not implemented: " + SegmentsCodec.class.getSimpleName() +
		                                        ".encode()");
	}

	private static Segment readSegment(FileBuffer buffer) {
		int v1 = buffer.getUnsignedShort();
		int v2 = buffer.getUnsignedShort();
		buffer.getShort(); // Unused angle
		int linedef  = buffer.getUnsignedShort();
		int backSide = buffer.getShort();
		buffer.getShort(); // Unused offset

		return new Segment(v1, v2, linedef, backSide != 0);
	}
}
