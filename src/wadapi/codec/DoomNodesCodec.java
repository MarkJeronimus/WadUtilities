package wadapi.codec;

import org.digitalmodular.utilities.annotation.UtilityClass;

import wadapi.FileBuffer;
import wadapi.LumpUtilities;
import wadapi.lump.FileBufferLump;
import wadapi.lump.NodesLump;
import wadapi.structure.Node;

/**
 * @author Zom-B
 */
// Created 2011-08-15
@UtilityClass
public class DoomNodesCodec {
	private static final int NODE_FIELD_SIZE = 28;

	public static NodesLump decode(FileBufferLump lump) {
		FileBuffer fileBuffer = lump.getFileBuffer();
		int        numNodes   = LumpUtilities.calcNumFields(fileBuffer.remaining(), NODE_FIELD_SIZE, lump.getName());

		NodesLump nodesLump = new NodesLump(lump.getName(), numNodes);

		for (int i = 0; i < numNodes; i++) {
			Node node = readNode(fileBuffer);
			nodesLump.add(node);
		}

		return nodesLump;
	}

	public static void encode(NodesLump nodes, FileBuffer buffer) {
		throw new UnsupportedOperationException("Not implemented: " + DoomNodesCodec.class.getSimpleName() +
		                                        ".encode()");
	}

	private static Node readNode(FileBuffer buffer) {
		int x       = buffer.getShort();
		int y       = buffer.getShort();
		int dx      = buffer.getShort();
		int dy      = buffer.getShort();
		int bbox0y2 = buffer.getShort();
		int bbox0y1 = buffer.getShort();
		int bbox0x1 = buffer.getShort();
		int bbox0x2 = buffer.getShort();
		int bbox1y2 = buffer.getShort();
		int bbox1y1 = buffer.getShort();
		int bbox1x1 = buffer.getShort();
		int bbox1x2 = buffer.getShort();
		int child0  = buffer.getShort();
		int child1  = buffer.getShort();
		return new Node(x, y, dx, dy,
		                bbox0y2, bbox0y1, bbox0x1, bbox0x2,
		                bbox1y2, bbox1y1, bbox1x1, bbox1x2,
		                child0, child1);
	}
}
