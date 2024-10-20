-- vec3.lua
local vec3 = {}
vec3.__index = vec3

-- Metatable for operator overloading
local vec3_mt = {}

--- Creates a new vec3 instance.
--- @param x number The x coordinate.
--- @param y number The y coordinate.
--- @param z number The z coordinate.
--- @return table A new vec3 object with x, y, and z coordinates.
function vec3.new(x, y, z)
    local v = setmetatable({ x = x or 0, y = y or 0, z = z or 0 }, vec3_mt)
    return v
end

--- Adds two vec3 vectors.
--- @param v1 table The first vec3 object.
--- @param v2 table The second vec3 object.
--- @return table The resulting vec3 after addition.
function vec3_mt.__add(v1, v2)
    return vec3.new(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z)
end

--- Subtracts one vec3 vector from another.
--- @param v1 table The first vec3 object.
--- @param v2 table The second vec3 object.
--- @return table The resulting vec3 after subtraction.
function vec3_mt.__sub(v1, v2)
    return vec3.new(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z)
end

--- Multiplies a vec3 vector by a scalar value or another vec3.
--- @param v1 table The first vec3 object.
--- @param v2 number or table The second value (can be a scalar or a vec3 object).
--- @return table The resulting vec3 after multiplication.
function vec3_mt.__mul(v1, v2)
    if type(v2) == "number" then
        return vec3.new(v1.x * v2, v1.y * v2, v1.z * v2)
    elseif getmetatable(v2) == vec3_mt then
        return vec3.new(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z)
    else
        error("Invalid operand for multiplication: must be a number or vec3")
    end
end

--- Divides a vec3 vector by a scalar value.
--- @param v1 table The vec3 object.
--- @param scalar number The scalar value to divide by.
--- @return table The resulting vec3 after division.
function vec3_mt.__div(v1, scalar)
    return vec3.new(v1.x / scalar, v1.y / scalar, v1.z / scalar)
end

--- Returns the negation of a vec3 vector.
--- @param v table The vec3 object.
--- @return table The resulting negated vec3.
function vec3_mt.__unm(v)
    return vec3.new(-v.x, -v.y, -v.z)
end

--- Converts the vec3 object to a string for debugging.
--- @param v table The vec3 object.
--- @return string A string representation of the vec3.
function vec3_mt.__tostring(v)
    return string.format("vec3(x = %.2f, y = %.2f, z = %.2f)", v.x, v.y, v.z)
end

--- Calculates the length (magnitude) of the vec3 vector.
--- @return number The length of the vector.
function vec3:length()
    return math.sqrt(self.x * self.x + self.y * self.y + self.z * self.z)
end

--- Normalizes the vec3 vector (makes its length 1).
--- @return table The normalized vec3 vector.
function vec3:normalize()
    local len = self:length()
    if len == 0 then
        return vec3.new(0, 0, 0)
    else
        return self / len
    end
end

--- Dot product of two vec3 vectors.
--- @param v2 table The second vec3 vector.
--- @return number The resulting dot product.
function vec3:dot(v2)
    return self.x * v2.x + self.y * v2.y + self.z * v2.z
end

--- Cross product of two vec3 vectors.
--- @param v2 table The second vec3 vector.
--- @return table The resulting vec3 after the cross product.
function vec3:cross(v2)
    return vec3.new(
        self.y * v2.z - self.z * v2.y,
        self.z * v2.x - self.x * v2.z,
        self.x * v2.y - self.y * v2.x
    )
end

-- Set vec3_mt as the metatable for vec3 objects
setmetatable(vec3, { __call = function(_, x, y, z) return vec3.new(x, y, z) end })
vec3_mt.__index = vec3

return vec3
