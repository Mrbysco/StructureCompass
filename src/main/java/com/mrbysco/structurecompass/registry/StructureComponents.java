package com.mrbysco.structurecompass.registry;

import com.mojang.serialization.Codec;
import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.component.StructureInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class StructureComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Reference.MOD_ID);

	public static final Supplier<DataComponentType<Identifier>> STRUCTURE = DATA_COMPONENT_TYPES.registerComponentType("structure", builder ->
			builder
					.persistent(Identifier.CODEC)
					.networkSynchronized(Identifier.STREAM_CODEC)
	);

	public static final Supplier<DataComponentType<Boolean>> IS_TAG = DATA_COMPONENT_TYPES.registerComponentType("is_tag", (builder) ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);

	public static final Supplier<DataComponentType<StructureInfo>> STRUCTURE_INFO = DATA_COMPONENT_TYPES.registerComponentType("structure_info", builder ->
			builder
					.persistent(StructureInfo.CODEC)
					.networkSynchronized(StructureInfo.STREAM_CODEC)
	);
}
