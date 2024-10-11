local playerPos = LuaCraft.getLocalPlayer().getPosition()
local allEntities = LuaCraft.getEntities()

for _, entity in ipairs(allEntities) do
	if entity.type == 'ZOMBIE' then
		entity.setPosition(playerPos)
		LuaCraft.modifyEntityData(entity.getUUID(), {
			isBaby = math.random(1, 2) == 1
		})
	end
end
