package wadapi.codec;

import org.jetbrains.annotations.Nullable;

import org.digitalmodular.utilities.annotation.UtilityClass;
import static org.digitalmodular.utilities.ValidatorUtilities.requireNonNull;

import wadapi.FileBuffer;
import wadapi.LumpUtilities;
import wadapi.WadMap;
import wadapi.io.LumpPointer;
import wadapi.io.MapPointers;
import wadapi.lump.FileBufferLump;
import wadapi.lump.Lump;

/**
 * @author Zom-B
 */
// Created 2021-08-02
@UtilityClass
public final class MapCodec {
	private MapCodec() {
		throw new AssertionError();
	}

	public static WadMap makeMap(MapPointers mapPointers) {
		requireNonNull(mapPointers, "mapPointers");

		boolean isHexenFormat = mapPointers.getBehaviorPointer() != null;
		if (isHexenFormat)
			Thread.yield();

		WadMapBuilder builder = new WadMapBuilder();
		builder.setMapLump(LumpUtilities.decodeLump(mapPointers.getMapTagPointer()));
		builder.setThingsLump(ThingsCodec.INSTANCE.decode(mapPointers.getThingsPointer()));
		builder.setLinedefsLump(LinedefsCodec.INSTANCE.decode(mapPointers.getLinedefsPointer()));
		builder.setSidedefsLump(SidedefsCodec.INSTANCE.decode(mapPointers.getSidedefsPointer()));
		builder.setVerticesLump(VerticesCodec.INSTANCE.decode(mapPointers.getVerticesPointer()));
		builder.setSectorsLump(SectorsCodec.INSTANCE.decode(mapPointers.getSectorsPointer()));

		@Nullable LumpPointer nodesPointer = mapPointers.getNodesPointer();
		if (nodesPointer != null) {
			Lump nodesLump = nodesPointer.getLump();

			if (nodesLump instanceof FileBufferLump) {
				FileBufferLump rawNodesLump = (FileBufferLump)nodesLump;

				NodeFormat nodeFormat = getNodeFormat(rawNodesLump);
				switch (nodeFormat) {
					case DOOM:
						builder.setSegmentsLump(SegmentsCodec.INSTANCE.decode(mapPointers.getSegmentsPointer()));
						builder.setSubsectorsLump(SubsectorsCodec.INSTANCE.decode(mapPointers.getSubsectorsPointer()));
						builder.setNodesLump(NodesCodec.decode(rawNodesLump));
						break;
					case XNOD:
						ExtendedNodesCodec.decode(rawNodesLump, builder);
						break;
					case ZNOD:
					case ZGLN:
					case ZGL2:
					case ZGL3:
					case XGLN:
					case XGL2:
					case XGL3:
					case XND4:
						throw new UnsupportedOperationException("Not implemented yet: " + nodeFormat);
					default:
						throw new AssertionError("Unimplemented NodeFormat: " + nodeFormat);
				}
			} else {
				builder.setNodesLump(nodesLump);
			}
		}

		builder.setRejectLump(BinaryCodec.INSTANCE.decode(mapPointers.getRejectPointer()));
		builder.setBlockmapLump(BinaryCodec.INSTANCE.decode(mapPointers.getBlockmapPointer()));
		builder.setScriptsLump(TextCodec.INSTANCE.decode(mapPointers.getScriptsPointer()));
		builder.setBehaviorLump(BinaryCodec.INSTANCE.decode(mapPointers.getBehaviorPointer()));

		return builder.build();
	}

	private static NodeFormat getNodeFormat(FileBufferLump lump) {
		FileBuffer fileBuffer = lump.getFileBuffer();

		fileBuffer.rewind();
		double size       = fileBuffer.remaining();
		int    signature0 = size < 4 ? 0 : fileBuffer.getInt();
		int    signature1 = size < 8 ? 0 : fileBuffer.getInt();
		fileBuffer.rewind();

		NodeFormat[] nodeFormats = NodeFormat.values();
		for (int i = nodeFormats.length - 1; i >= 0; i--) {
			NodeFormat format = nodeFormats[i];

			if (signatureMatches(size, signature0, signature1, format))
				return format;
		}

		throw new AssertionError(size + ", " + signature0 + ", " + signature1);
	}

	private static boolean signatureMatches(double size, int signature0, int signature1, NodeFormat format) {
		int[] signature = format.getSignature();

		if (signature.length >= 2 && (size < 8 || signature[1] != signature1))
			return false;

		if (signature.length >= 1 && (size < 4 || signature[0] != signature0))
			return false;

		return true;
	}
}
