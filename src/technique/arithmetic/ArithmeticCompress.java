package technique.arithmetic;

class ArithmeticCompress {
    protected static final int NO_OF_CHARS = 256;
    protected static final int NO_OF_SYMBOLS = 257;
    protected static final int MAX_FREQ = 16383;

    protected int[] char_to_index = new int[NO_OF_CHARS];
    protected int[] index_to_char = new int[NO_OF_SYMBOLS];
    protected int[] freq = new int[NO_OF_SYMBOLS + 1];
    protected int[] cum_freq = new int[NO_OF_SYMBOLS + 1];

    public ArithmeticCompress() {
        for (int i = 0; i < NO_OF_CHARS; i++) {
            char_to_index[i] = i + 1;
            index_to_char[i + 1] = i;
        }
        for (int i = 0; i <= NO_OF_SYMBOLS; i++) {
            freq[i] = 1;
            cum_freq[i] = NO_OF_SYMBOLS - i;
        }
        freq[0] = 0;
    }

    protected void update_tables(int sym_index) {
        if (cum_freq[0] == MAX_FREQ) {
            int cum = 0;
            for (int i = NO_OF_SYMBOLS; i >= 0; i--) {
                freq[i] = (freq[i] + 1) / 2;
                cum_freq[i] = cum;
                cum += freq[i];
            }
        }
        int i;
        for (i = sym_index; freq[i] == freq[i - 1]; i--);
        if (i < sym_index) {
            int ch_i = index_to_char[i];
            int ch_symbol = index_to_char[sym_index];
            index_to_char[i] = ch_symbol;
            index_to_char[sym_index] = ch_i;
            char_to_index[ch_i] = sym_index;
            char_to_index[ch_symbol] = i;
        }
        freq[i]++;
        while (i > 0) {
            i--;
            cum_freq[i]++;
        }
    }
}
