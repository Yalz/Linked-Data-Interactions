interface PropertyListProps {
    component: Component;
    type: "INPUT" | "ADAPTER" | "TRANSFORMER" | "OUTPUT"
}

interface Component {
    name: String;
    class: String;
    description: String;
    properties: any[]
}

export const LdioComponent: React.FC<PropertyListProps> = ({ component, type }) => {
    return (
        <tr className="border-t border-slate-300">
            <td className="p-2 align-top">
                <div className="flex flex-col items-center">
                    <div className="h-8 w-8 rounded-full bg-teal-500 text-white flex items-center justify-center text-xs font-bold">
                        {component.name.split(':')[0].toUpperCase()}
                    </div>
                    <div className="mt-1 text-slate-700">{component.name}</div>
                </div>

                {/* Tooltip shown when hovering over the entire cell */}
                <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-full bg-slate-800 text-white text-xs rounded px-2 py-1 whitespace-nowrap z-10 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                    {component.class}
                </div>
            </td>
            <td className="p-2 align-top">
                <div className="flex flex-col items-center">
                    <div className="font-semibold">{component.name.split(':')[0]}</div>
                    <div className="text-xs text-slate-500">{type}</div>
                </div>
            </td>
            <td className="p-2 align-top text-slate-600 overflow-auto">{component.description}</td>
            <td className="p-2 align-top">
                <div className="max-h-64 overflow-auto">
                    <table className="w-full table-fixed text-xs border border-slate-200">
                        <thead className="bg-slate-50">
                            <tr>
                                <th className="p-1 border">Key</th>
                                <th className="p-1 border">Type</th>
                                <th className="p-1 border">Default</th>
                                <th className="p-1 border">Required</th>
                            </tr>
                        </thead>
                        <tbody>
                            {component.properties?.length ? (
                                component.properties.map((prop, index) => (
                                    <tr key={index}>
                                        <td className="p-1 border overflow-auto">{prop.key}</td>
                                        <td className="p-1 border overflow-auto">{prop.expectedType}</td>
                                        <td className="p-1 border overflow-auto">{prop.defaultValue}</td>
                                        <td className="p-1 border overflow-auto">
                                            <span
                                                className={`px-2 py-1 rounded text-xs font-bold uppercase ${prop.required
                                                    ? 'bg-red-100 text-red-700'
                                                    : 'bg-yellow-100 text-yellow-700'
                                                    }`}
                                            >
                                                {prop.required ? 'Required' : 'Optional'}
                                            </span>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan={4} className="p-2 text-slate-400 italic">
                                        No properties defined
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>

            </td>
        </tr>
    );
};