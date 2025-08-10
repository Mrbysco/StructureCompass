package com.mrbysco.structurecompass.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record StructureInfo(GlobalPos globalPos) {
	public static final Codec<StructureInfo> CODEC = RecordCodecBuilder.create(inst -> inst.group(
					GlobalPos.CODEC.fieldOf("globalPos").forGetter(StructureInfo::globalPos))
			.apply(inst, StructureInfo::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, StructureInfo> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC,
			StructureInfo::globalPos,
			StructureInfo::new
	);
}
