/*
 * Copyright (c) 2010-2021 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 */
package smile.plot.vega;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import smile.util.Strings;

/**
 * Vega-Lite specifications are JSON objects that describe a diverse range
 * of interactive visualizations. Besides using a single view specification
 * as a standalone visualization, Vega-Lite also provides operators for
 * composing multiple view specifications into a layered or multi-view
 * specification. These operators include layer, facet, concat, and repeat.
 *
 * @author Haifeng Li
 */
public class VegaLite {
    /**
     * The schema of Vega-Lite.
     */
    private static String schema = "https://vega.github.io/schema/vega-lite/v5.json";
    /**
     * The MIME type of Vega-Lite.
     */
    private static String mime = "application/vnd.vegalite.v5+json";
    /**
     * JSON object mapping.
     */
    final ObjectMapper mapper = new ObjectMapper();
    /**
     * The Vega-Lite specification.
     */
    final ObjectNode spec = mapper.createObjectNode();
    /**
     * The configuration object lists configuration properties
     * of a visualization for creating a consistent theme.
     */
    final ObjectNode config = spec.putObject("config");
    /**
     * Default properties for single view plots.
     */
    final ObjectNode view = config.putObject("view");

    /**
     * Constructor.
     */
    public VegaLite() {
        spec.put("$schema", schema);
        view.put("continuousWidth", 400);
        view.put("continuousHeight", 400);
    }

    @Override
    public String toString() {
        return spec.toString();
    }

    /**
     * Returns the specification in pretty print.
     * @return the specification in pretty print.
     */
    public String toPrettyString() {
        return spec.toPrettyString();
    }

    // ====== Properties of Top-Level Specifications ======
    /**
     * Returns the configuration object that lists properties of
     * a visualization for creating a consistent theme. This property
     * can only be defined at the top-level of a specification.
     */
    public Config config() {
        return new Config(config);
    }

    /**
     * Returns the configuration object defining the style of
     * a single view visualization.
     */
    public ViewConfig viewConfig() {
        return new ViewConfig(view);
    }

    /**
     * Optional metadata that will be passed to Vega. This object is completely
     * ignored by Vega and Vega-Lite and can be used for custom metadata.
     */
    public VegaLite usermeta(JsonNode metadata) {
        spec.set("usermeta", metadata);
        return this;
    }

    /**
     * Optional metadata that will be passed to Vega. This object is completely
     * ignored by Vega and Vega-Lite and can be used for custom metadata.
     */
    public VegaLite usermeta(Object metadata) {
        spec.putPOJO("usermeta", metadata);
        return this;
    }

    /**
     * Sets the background of the entire view with CSS color property.
     */
    public VegaLite background(String color) {
        spec.put("background", color);
        return this;
    }

    /**
     * Specifies padding for all sides.
     * The visualization padding, in pixels, is from the edge of the
     * visualization canvas to the data rectangle.
     */
    public VegaLite padding(int size) {
        spec.put("padding", size);
        return this;
    }

    /**
     * Specifies padding for each side.
     * The visualization padding, in pixels, is from the edge of the
     * visualization canvas to the data rectangle.
     */
    public VegaLite padding(int left, int top, int right, int bottom) {
        ObjectNode padding = spec.putObject("padding");
        padding.put("left", left);
        padding.put("top", top);
        padding.put("right", right);
        padding.put("bottom", bottom);
        return this;
    }

    /**
     * Sets the overall size of the visualization. The total size of
     * a Vega-Lite visualization may be determined by multiple factors:
     * specified width, height, and padding values, as well as content
     * such as axes, legends, and titles.
     */
    public VegaLite autosize() {
        return autosize("pad", false, "content");
    }

    /**
     * Sets the overall size of the visualization. The total size of
     * a Vega-Lite visualization may be determined by multiple factors:
     * specified width, height, and padding values, as well as content
     * such as axes, legends, and titles.
     *
     * @param type     The sizing format type. One of "pad", "fit", "fit-x",
     *                 "fit-y", or "none". See Vega-Lite documentation for
     *                 descriptions of each.
     * @param resize   A boolean flag indicating if autosize layout should
     *                 be re-calculated on every view update.
     * @param contains Determines how size calculation should be performed,
     *                 one of "content" or "padding". The default setting
     *                 ("content") interprets the width and height settings
     *                 as the data rectangle (plotting) dimensions, to which
     *                 padding is then added. In contrast, the "padding"
     *                 setting includes the padding within the view size
     *                 calculations, such that the width and height settings
     *                 indicate the total intended size of the view.
     * @link https://vega.github.io/vega-lite/docs/size.html#autosize
     */
    public VegaLite autosize(String type, boolean resize, String contains) {
        ObjectNode autosize = spec.putObject("autosize");
        autosize.put("type", type);
        autosize.put("resize", resize);
        autosize.put("contains", contains);
        return this;
    }

    // ====== Common Properties ======

    /**
     * Sets the name of the visualization for later reference.
     */
    public VegaLite name(String name) {
        spec.put("name", name);
        return this;
    }

    /**
     * Sets the description of this mark for commenting purpose.
     */
    public VegaLite description(String description) {
        spec.put("description", description);
        return this;
    }

    /**
     * Sets a descriptive title to a chart.
     */
    public VegaLite title(String title) {
        spec.put("title", title);
        return this;
    }

    /**
     * Sets an array describing the data source. Set to null to ignore
     * the parent's data source. If no data is set, it is derived from
     * the parent.
     */
    public VegaLite data(JsonNode data) {
        if (data == null) {
            spec.remove("data");
        } else {
            ObjectNode node = spec.putObject("data");
            node.set("values", data);
        }
        return this;
    }

    /**
     * Sets an array describing the data source. Set to null to ignore
     * the parent's data source. If no data is set, it is derived from
     * the parent.
     * @param data JSON content to parse.
     */
    public VegaLite json(String data) throws JsonProcessingException {
        return data(mapper.readTree(data));
    }

    /**
     * Sets an array describing the data source. Set to null to ignore
     * the parent's data source. If no data is set, it is derived from
     * the parent.
     */
    public <T> VegaLite data(T[] data) {
        if (data == null) {
            spec.remove("data");
        } else {
            ObjectNode node = spec.putObject("data");
            node.set("values", mapper.valueToTree(data));
        }
        return this;
    }

    /**
     * Sets a list describing the data source. Set to null to ignore
     * the parent's data source. If no data is set, it is derived from
     * the parent.
     */
    public <T> VegaLite data(List<T> data) {
        if (data == null) {
            spec.remove("data");
        } else {
            ObjectNode node = spec.putObject("data");
            node.set("values", mapper.valueToTree(data));
        }
        return this;
    }

    /**
     * Sets the url of the data source. The default format type is determined
     * by the extension of the file URL. If no extension is detected, "json"
     * will be used by default.
     *
     * @param url A URL from which to load the data set.
     */
    public VegaLite data(String url) {
        ObjectNode node = spec.putObject("data");
        node.put("url", url);
        return this;
    }

    /**
     * Sets the url of the data source.
     *
     * @param url    A URL from which to load the data set.
     * @param format File format: "json", "csv", "tsv", "dsv".
     * @return the data format specification object.
     */
    private ObjectNode data(String url, String format) {
        ObjectNode node = spec.putObject("data");
        node.put("url", url);
        ObjectNode dataFormat = node.putObject("format");
        dataFormat.put("type", format);
        return dataFormat;
    }

    /**
     * Loads a JSON file. Assumes row-oriented data, where each row is an
     * object with named attributes.
     *
     * @param url    A URL from which to load the data set.
     * @param property The JSON property containing the desired data. This
     *                parameter can be used when the loaded JSON file may
     *                have surrounding structure or meta-data. For example
     *                "values.features" is equivalent to retrieving
     *                json.values.features from the loaded JSON object.
     */
    public VegaLite json(String url, String property) {
        ObjectNode format = data(url, "json");
        format.put("property", property);
        return this;
    }

    /**
     * Loads a JSON file using the TopoJSON format. The input file must
     * contain valid TopoJSON data. The TopoJSON input is then converted
     * into a GeoJSON format. There are two mutually exclusive properties
     * that can be used to specify the conversion process: "feature" or
     * "mesh".
     *
     * @param url    A URL from which to load the data set.
     * @param conversion "feature" or "mesh".
     * @param name The name of the TopoJSON object set to convert to a
     *            GeoJSON feature collection (or mesh).
     */
    public VegaLite topojson(String url, String conversion, String name) {
        ObjectNode format = data(url, "topojson");
        format.put(conversion, name);
        return this;
    }

    /**
     * Sets explicit data types.
     * @param format the data format specification object.
     * @param type If set to null, disable type inference based on the spec
     *            and only use type inference based on the data.
     */
    private void dataType(ObjectNode format, Map<String, String> type) {
        if (type == null) {
            format.putNull("parse");
        } else {
            ObjectNode parse = format.putObject("parse");
            for (Map.Entry<String, String> entry : type.entrySet()) {
                parse.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Loads a comma-separated values (CSV) file
     *
     * @param url    A URL from which to load the data set.
     */
    public VegaLite csv(String url) {
        ObjectNode format = data(url, "csv");
        return this;
    }

    /**
     * Loads a comma-separated values (CSV) file
     *
     * @param url    A URL from which to load the data set.
     * @param dataTypes If set to null, disable type inference based on the spec
     *                 and only use type inference based on the data.
     *                 Alternatively, a parsing directive object for explicit
     *                 data types. Each property of the object corresponds to a
     *                 field name, and the value to the desired data type (one
     *                 of "number", "boolean", "date", or null (do not parse
     *                 the field)). For "date", we parse data based using
     *                 JavaScript's Date.parse(). For Specific date formats
     *                 can be provided (e.g., {foo: "date:'%m%d%Y'"}), using
     *                 the d3-time-format syntax. UTC date format parsing is
     *                 supported similarly (e.g., {foo: "utc:'%m%d%Y'"}).
     */
    public VegaLite csv(String url, Map<String, String> dataTypes) {
        ObjectNode format = data(url, "csv");
        dataType(format, dataTypes);
        return this;
    }

    /**
     * Loads a tab-separated values (TSV) file
     *
     * @param url    A URL from which to load the data set..
     */
    public VegaLite tsv(String url) {
        ObjectNode format = data(url, "tsv");
        return this;
    }

    /**
     * Loads a tab-separated values (TSV) file
     *
     * @param url    A URL from which to load the data set.
     * @param dataTypes A parsing directive object for explicit data types.
     *                 Each property of the object corresponds to a field name,
     *                 and the value to the desired data type (one of "number",
     *                 "boolean", "date", or null (do not parse the field)).
     *                  For "date", we parse data based using JavaScript's
     *                  Date.parse(). For Specific date formats can be provided
     *                  (e.g., {foo: "date:'%m%d%Y'"}), using the d3-time-format
     *                  syntax. UTC date format parsing is supported similarly
     *                  (e.g., {foo: "utc:'%m%d%Y'"}).
     */
    public VegaLite tsv(String url, Map<String, String> dataTypes) {
        ObjectNode format = data(url, "tsv");
        dataType(format, dataTypes);
        return this;
    }

    /**
     * Loads a delimited text file with a custom delimiter.
     * This is a general version of CSV and TSV.
     *
     * @param url    A URL from which to load the data set.
     * @param delimiter The delimiter between records. The delimiter must be
     *                 a single character (i.e., a single 16-bit code unit);
     *                 so, ASCII delimiters are fine, but emoji delimiters
     *                 are not.
     */
    public VegaLite dsv(String url, String delimiter) {
        ObjectNode format = data(url, "dsv");
        format.put("delimiter", delimiter);
        return this;
    }

    /**
     * Loads a delimited text file with a custom delimiter.
     * This is a general version of CSV and TSV.
     *
     * @param url    A URL from which to load the data set.
     * @param delimiter The delimiter between records. The delimiter must be
     *                 a single character (i.e., a single 16-bit code unit);
     *                 so, ASCII delimiters are fine, but emoji delimiters
     *                 are not.
     * @param dataTypes A parsing directive object for explicit data types.
     *                 Each property of the object corresponds to a field name,
     *                 and the value to the desired data type (one of "number",
     *                 "boolean", "date", or null (do not parse the field)).
     *                  For "date", we parse data based using JavaScript's
     *                  Date.parse(). For Specific date formats can be provided
     *                  (e.g., {foo: "date:'%m%d%Y'"}), using the d3-time-format
     *                  syntax. UTC date format parsing is supported similarly
     *                  (e.g., {foo: "utc:'%m%d%Y'"}).
     */
    public VegaLite dsv(String url, String delimiter, Map<String, String> dataTypes) {
        ObjectNode format = data(url, "dsv");
        format.put("delimiter", delimiter);
        dataType(format, dataTypes);
        return this;
    }

    /**
     * Returns the data transformation object.
     * such as filter and new field
     * calculation. Data transformations in Vega-Lite are described
     * via either view-level transforms (the transform property) or
     * field transforms inside encoding (bin, timeUnit, aggregate,
     * sort, and stack).
     * <p>
     * When both types of transforms are specified, the view-level
     * transforms are executed first based on the order in the
     * array. Then the inline transforms are executed in this order:
     * bin, timeUnit, aggregate, sort, and stack.
     * @return the data transformation object.
     */
    public Transform transform() {
        ArrayNode node = spec.has("transform") ? (ArrayNode) spec.get("transform") : spec.putArray("transform");
        return new Transform(node);
    }

    /**
     * Displays the plot with the default browser.
     */
    public void show() throws IOException, JsonProcessingException {
        Path path = Files.createTempFile("smile-plot-", ".html");
        path.toFile().deleteOnExit();
        Files.write(path, embed().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        java.awt.Desktop.getDesktop().browse(path.toUri());
    }

    /**
     * Returns the HTML of plot specification with Vega Embed.
     */
    public String embed() throws JsonProcessingException {
        return embed("en");
    }

    /**
     * Returns the HTML of plot specification with Vega Embed.
     * @param lang the primary language of document.
     * @return the HTML of plot specification with Vega Embed.
     */
    public String embed(String lang) throws JsonProcessingException {
        String title = spec.has("title") ? spec.get("title").asText() : "Smile Plot";
        return String.format("""
                   <!DOCTYPE html>
                   <html lang=%s>
                   <head>
                     <title>%s</title>
                     <meta name="viewport" content="width=device-width, initial-scale=1">
                     <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega@5"></script>
                     <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega-lite@5"></script>
                     <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega-embed@6"></script>
                   </head>
                   <body>
                   
                   <div id="vega-lite"></div>
                   
                   <script type="text/javascript">
                     var spec = %s;
                     var opt = {
                       "mode": "vega-lite",
                       "renderer": "canvas",
                       "actions": {"editor": true, "source": true, "export": true}
                     };
                     vegaEmbed('#vega-lite', spec, opt).catch(console.error);
                   </script>
                   </body>
                   </html>
                """, lang, title, spec.toPrettyString());
    }

    /**
     * Returns the HTML wrapped in an iframe to render in notebooks.
     */
    public String iframe() throws JsonProcessingException {
        return iframe(UUID.randomUUID().toString());
    }

    /**
     * Returns the HTML wrapped in an iframe to render in notebooks.
     *
     * @param id the iframe HTML id.
     */
    public String iframe(String id) throws JsonProcessingException {
        String src = Strings.htmlEscape(embed());
        return String.format("""
                   <iframe id="%s" sandbox="allow-scripts allow-same-origin" style="border: none; width: 100%" srcdoc="%s"></iframe>
                   <script>
                     (function() {
                       function resizeIFrame(el, k) {
                         var height = el.contentWindow.document.body.scrollHeight || '600'; // Fallback in case of no scroll height
                         el.style.height = height + 'px';
                         if (k <= 10) { setTimeout(function() { resizeIFrame(el, k+1) }, 100 + (k * 250)) };
                       }
                       resizeIFrame(document.getElementById("%s"), 1);
                     })(); // IIFE
                   </script>
                """, id, src, id);
    }

    /** Returns the encoding object. */
    ObjectNode encoding() {
        return spec.has("encoding") ? (ObjectNode) spec.get("encoding") : spec.putObject("encoding");
    }
}