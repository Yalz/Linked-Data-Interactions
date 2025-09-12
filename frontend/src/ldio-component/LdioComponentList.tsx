import axios from "axios";
import { LdioComponent } from "./LdioComponent"
import { useEffect, useState } from "react";

export const LdioComponentList: React.FC = (() => {
    const [inputs, setInputs] = useState<any[]>([]);
    const [adapters, setAdapters] = useState<any[]>([]);
    const [transformers, setTransformers] = useState<any[]>([]);
    const [outputs, setOutputs] = useState<any[]>([]);

    useEffect(() => {
        // Make GET request to fetch data
        axios
            .get("http://localhost:8080/config")
            .then((response) => {
                setInputs(response.data.inputs);
                setAdapters(response.data.adapters);
                setTransformers(response.data.transformers);
                setOutputs(response.data.outputs);
            })
            .catch((err) => {
            });
    }, []);

    return (
        <table className="w-full mt-4 table-fixed text-sm text-center border border-slate-300">
            <colgroup>
                <col className="w-[20%]" />
                <col className="w-[15%]" />
                <col className="w-[25%]" />
                <col className="w-[40%]" />
            </colgroup>
            <thead className="bg-slate-100">
                <tr>
                    <th className="p-2 border">Component</th>
                    <th className="p-2 border">Type</th>
                    <th className="p-2 border">Description</th>
                    <th className="p-2 border">Properties</th>
                </tr>
            </thead>

            {/* Inputs Group */}
            <tbody>
                <tr className="bg-slate-200">
                    <td colSpan={4} className="p-2 font-semibold text-left text-slate-700">
                        Inputs
                    </td>
                </tr>
                {inputs.map(component => (
                    <LdioComponent key={component.name} component={component} type="INPUT" />
                ))}
            </tbody>

            {/* Adapters Group */}
            <tbody>
                <tr className="bg-slate-200">
                    <td colSpan={4} className="p-2 font-semibold text-left text-slate-700">
                        Adapters
                    </td>
                </tr>
                {adapters.map(component => (
                    <LdioComponent key={component.name} component={component} type="ADAPTER" />
                ))}
            </tbody>

            {/* Transformers Group */}
            <tbody>
                <tr className="bg-slate-200">
                    <td colSpan={4} className="p-2 font-semibold text-left text-slate-700">
                        Transformers
                    </td>
                </tr>
                {transformers.map(component => (
                    <LdioComponent key={component.name} component={component} type="TRANSFORMER" />
                ))}
            </tbody>

            {/* Outputs Group */}
            <tbody>
                <tr className="bg-slate-200">
                    <td colSpan={4} className="p-2 font-semibold text-left text-slate-700">
                        Outputs
                    </td>
                </tr>
                {outputs.map(component => (
                    <LdioComponent key={component.name} component={component} type="OUTPUT" />
                ))}
            </tbody>
        </table>
    );

})