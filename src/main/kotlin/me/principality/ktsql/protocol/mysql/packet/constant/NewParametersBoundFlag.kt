package me.principality.ktsql.protocol.mysql.packet.constant

enum class NewParametersBoundFlag(val value: Int) {
    PARAMETER_TYPE_EXIST(1),
    PARAMETER_TYPE_NOT_EXIST(0);

    /**
     * Value of new parameters bound flag.
     *
     * @param value value
     * @return new parameters bound flag
     */
    fun valueOf(value: Int): NewParametersBoundFlag {
        for (each in NewParametersBoundFlag.values()) {
            if (value == each.value) {
                return each
            }
        }
        throw IllegalArgumentException(String.format("Cannot find value '%s' in new parameters bound flag", value))
    }}